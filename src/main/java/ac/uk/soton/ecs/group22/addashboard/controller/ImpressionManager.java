package ac.uk.soton.ecs.group22.addashboard.controller;

import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionLoader;
import ac.uk.soton.ecs.group22.addashboard.events.LoadDataEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The ImpressionManager stores the loaded impression data.
 * <p>
 * It is also responsible for returning the impressions which match the filter.
 */
public class ImpressionManager extends AbstractManager {

  // ID -> Impression
  private final Map<Long, Set<ImpressionEntry>> impressions;

  public ImpressionManager() {
    this.impressions = new LinkedHashMap<>();

    LoadDataEvent.getListeners().add(loadDataEvent -> hashedFilter = Integer.MIN_VALUE);
  }

  /**
   * @return All the impressinos, regardless of filter.
   */
  public List<ImpressionEntry> getImpressions() {
    return impressions.values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * The loader will only read files, it needs to be used in conjunction with setImpression.
   *
   * @param file A file containing the impressions.
   * @return The loader responsible for loading in from the files.
   */
  public ImpressionLoader createLoader(File file) {
    return new ImpressionLoader(file);
  }

  /**
   * Clears the cached impressions, copies in from the loader and sets state to loaded in.
   *
   * @param loader The loader containing the loaded impressions.
   */
  public void setImpressions(ImpressionLoader loader) {
    this.impressions.clear();

    for (ImpressionEntry impressionEntry : loader.getImpressions()) {
      Set<ImpressionEntry> impressionsSet = impressions.computeIfAbsent(impressionEntry.getId(), k -> new HashSet<>());
      impressionsSet.add(impressionEntry);
    }

    setHasLoadedInData(true);
  }

  private List<ImpressionEntry> matches;
  private int hashedFilter = -1;

  /**
   * @return A list of impressions which match the filter.
   */
  public List<ImpressionEntry> fetchMatches() {
    int newHash = Campaign.getInstance().getImpressionFilter().hashCode() + Campaign.getInstance().getDateFilter().hashCode();

    if (hashedFilter == newHash) {
      return matches;
    }

    matches = getImpressions().stream()
        .filter(Campaign.getInstance().getImpressionFilter()::matches)
        .filter(impression -> Campaign.getInstance().getDateFilter().matches(impression.getDate()))
        .toList();
    hashedFilter = newHash;

    return matches;
  }

  /**
   * returns the entries that match the given filters
   * @param impressionFilter the given impression filter
   * @param dateFilter the given date filter
   * @return the matches
   */
  public List<ImpressionEntry> fetchMatches(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return getImpressions().stream()
        .filter(impressionFilter::matches)
        .filter(impression -> dateFilter.matches(impression.getDate()))
        .toList();
  }

  private int uniqueCountCache;

  /**
   * @return The number of unique visitors.
   */
  public int getUniqueCount() {
    uniqueCountCache = (int) fetchMatches().stream()
        .mapToLong(ImpressionEntry::getId)
        .distinct()
        .count();

    return uniqueCountCache;
  }

  /**
   * @return The number of unique visitors.
   */
  public int getUniqueCount(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    uniqueCountCache = (int) fetchMatches(impressionFilter, dateFilter).stream()
        .mapToLong(ImpressionEntry::getId)
        .distinct()
        .count();

    return uniqueCountCache;
  }

  public List<ImpressionEntry> fetchUniqueImpressions() {
    List<ImpressionEntry> toReturn = new ArrayList<>();
    Set<Long> doneIds = new HashSet<>();
    for(ImpressionEntry entry : fetchMatches()) {
      if (doneIds.contains(entry.getId())) {
        continue;
      }
      doneIds.add(entry.getId());
      toReturn.add(entry);
    }
    return toReturn;
  }

  public List<ImpressionEntry> fetchUniqueImpressions(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    List<ImpressionEntry> toReturn = new ArrayList<>();
    Set<Long> doneIds = new HashSet<>();

    for(ImpressionEntry entry : fetchMatches(impressionFilter, dateFilter)) {
      if (doneIds.contains(entry.getId())) {
        continue;
      }

      doneIds.add(entry.getId());
      toReturn.add(entry);
    }

    return toReturn;
  }

  /**
   * Remove all impression data and reset to default state (i.e. data not loaded in).
   */
  public void clear() {
    impressions.clear();

    setHasLoadedInData(false);
  }

}
