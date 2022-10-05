package ac.uk.soton.ecs.group22.addashboard.controller;

import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickLoader;

import ac.uk.soton.ecs.group22.addashboard.data.csv.click.CostBin;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;

import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerEntry;
import ac.uk.soton.ecs.group22.addashboard.events.LoadDataEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <h1> Click Manager </h1>
 * The controller class for the click log, provides a bridge between the GUI and the data
 *
 * @since 2022-03-05
 * @version 1.0
 * @see AbstractManager
 */
public class ClickManager extends AbstractManager {

  // ID -> Click
  private final Map<Long, Set<ClickEntry>> clicks;

  /**
   * Constructor, initialises the clicks map
   */
  public ClickManager() {
    this.clicks = new LinkedHashMap<>();

    LoadDataEvent.getListeners().add(loadDataEvent -> hashedFilter = Integer.MIN_VALUE);
  }

  /**
   * Creates and returns a ClickLoader for the given file
   * @param file the given file
   * @return ClickLoader a ClickLoader for the given file
   * @see ClickLoader
   */
  public ClickLoader createLoader(File file) {
    return new ClickLoader(file);
  }

  /**
   * Clears and updates the clicks entry
   * @param loader the loader to get the data from
   * @see ClickLoader
   */
  public void setClicks(ClickLoader loader) {
    this.clicks.clear();

    for(ClickEntry clickEntry : loader.getClicks()) {
      Set<ClickEntry> impressionsSet = clicks.computeIfAbsent(clickEntry.getId(), k -> new HashSet<>());
      impressionsSet.add(clickEntry);
    }

    setHasLoadedInData(true);
  }

  public List<ClickEntry> getClicks() {
    return clicks.values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * Calculates the total click cost of the filtered dataset
   * @param filter To apply the impression filter or not.
   * @return double The sum of all clicks
   */
  public double getTotalCost(boolean filter) {
    return (filter ? fetchMatches(filter) : getClicks())
        .stream()
        .reduce(0.0, (subtotal, next) -> subtotal + next.getClickCost(), Double::sum);
  }

  /**
   * Calculates the total click cost of the filtered dataset
   * @return double The sum of all clicks
   */
  public double getTotalCost(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return fetchMatches(impressionFilter, dateFilter)
        .stream()
        .reduce(0.0, (subtotal, next) -> subtotal + next.getClickCost(), Double::sum);
  }

  private List<ClickEntry> matches;
  private int hashedFilter = -1;

  /**
   * Returns the list of click entries that pass the filter
   * @return List<ClickEntry> The filtered click entries
   */
  public List<ClickEntry> fetchMatches(Boolean filter) {
    if(!filter) {
      return new ArrayList<>(getClicks());
    }

    int newHash = Campaign.getInstance().getImpressionFilter().hashCode() + Campaign.getInstance().getDateFilter().hashCode();

    if (hashedFilter == newHash) {
      return matches;
    }

    List<ImpressionEntry> impressions = Campaign.getInstance().getImpressionManager().fetchMatches();

    matches = impressions.stream()
        .filter(impression -> clicks.containsKey(impression.getId()))
        .flatMap(impression -> clicks.get(impression.getId()).stream())
        .filter(click -> Campaign.getInstance().getDateFilter().matches(click.getDate()))
        .collect(Collectors.toList());
    hashedFilter = newHash;

    return matches;
  }

  /**
   * returns the clicks entries that pass the given filter
   * @param dateFilter the date filter
   * @param impressionFilter the impression filter
   */
  public List<ClickEntry> fetchMatches(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return Campaign.getInstance().getImpressionManager().fetchMatches(impressionFilter, dateFilter).stream()
        .filter(impression -> clicks.containsKey(impression.getId()))
        .flatMap(impression -> clicks.get(impression.getId()).stream())
        .filter(click -> dateFilter.matches(click.getDate()))
        .collect(Collectors.toList());
  }

  /**
   * Maps the clicks into different cost categories
   * @return Map<CostBin, Integer> The mapping
   * @see CostBin
   */
  public Map<CostBin, Integer> getCostFrequencies(Boolean filter) {
    Map<CostBin, Integer> frequencies = new HashMap<>();
    fetchMatches(filter).forEach(c -> frequencies.compute(CostBin.getBin(c.getClickCost()), (k, v) -> v == null ? 1 : v + 1));
    for (CostBin bin: CostBin.values()) {
      frequencies.putIfAbsent(bin, 0);
    }
    return frequencies;
  }

  /**
   * Maps the clicks into different cost categories
   */
  public Map<CostBin, Integer> getCostFrequencies(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    Map<CostBin, Integer> frequencies = new HashMap<>();
    fetchMatches(impressionFilter, dateFilter).forEach(c -> frequencies.compute(CostBin.getBin(c.getClickCost()), (k, v) -> v == null ? 1 : v + 1));
    for (CostBin bin: CostBin.values()) {
      frequencies.putIfAbsent(bin, 0);
    }
    return frequencies;
  }

  /**
   * Clears the click entries
   */
  public void clear() {
    clicks.clear();

    setHasLoadedInData(false);
  }

  /**
   * Calculates the CTR
   * @return BigDecimal The CTR
   */
  public BigDecimal getCTR(Boolean filter) {
    return division(getTotalClicks(filter),getImpressions(),2);
  }

  public BigDecimal getCTR(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return division(getTotalClicks(impressionFilter, dateFilter),getImpressions(),2);
  }

  /**
   * Calculates the CPC
   * @return BigDecimal The CPC
   */
  public BigDecimal getCPC(Boolean filter) {
    return division(getCost(filter),getTotalClicks(filter),3);
  }

  public BigDecimal getCPC(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return division(getCost(impressionFilter, dateFilter),getTotalClicks(impressionFilter, dateFilter),3);
  }

  /**
   * Calculates the CPM
   * @return BigDecimal The CPM
   */
  public BigDecimal getCPM(Boolean filter) {
    BigDecimal multiply = getCost(filter).multiply(BigDecimal.valueOf(1000));
    return division(multiply,getImpressions(),3);
  }

  public BigDecimal getCPM(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    BigDecimal multiply = getCost(impressionFilter, dateFilter).multiply(BigDecimal.valueOf(1000));
    return division(multiply,getImpressions(),3);
  }
  /**
   * Calculates the CPA
   * @return BigDecimal The CPA
   */
  public BigDecimal getCPA(Boolean filter) {
    return division(getCost(filter), getConversions(filter), 3);
  }

  public BigDecimal getCPA(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return division(getCost(impressionFilter, dateFilter), getConversions(impressionFilter, dateFilter), 3);
  }

  /**
   * Divides and rounds the result to a number of decimal places
   * @param dividend The dividend
   * @param divisor The divisor
   * @param scale The number of decimal places
   * @return BigDecimal The result
   */
  private BigDecimal division(BigDecimal dividend, BigDecimal divisor, int scale) {
    if (divisor.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.valueOf(0.00);
    } else {
      return (dividend.divide(divisor,scale, RoundingMode.HALF_UP));
    }
  }

  /**
   * Gets the number of impression entries
   * @return BigDecimal The number
   */
  private BigDecimal getImpressions() {
    return BigDecimal.valueOf(Campaign.getInstance().getImpressionManager().fetchMatches().size());
  }

  private BigDecimal getImpressions(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return BigDecimal.valueOf(Campaign.getInstance().getImpressionManager().fetchMatches(impressionFilter, dateFilter).size());
  }

  /**
   * Gets the number of click entries
   * @return BigDecimal The number
   */
  private BigDecimal getTotalClicks(Boolean filter) {
    return BigDecimal.valueOf(fetchMatches(filter).size());
  }

  public BigDecimal getTotalClicks(List<ClickEntry> clickEntry) {
    return BigDecimal.valueOf(clickEntry.size());
  }

  public BigDecimal getTotalClicks(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return BigDecimal.valueOf(fetchMatches(impressionFilter, dateFilter).size());
  }

  /**
   * Gets the number of conversions
   * @return BigDecimal The number
   */
  private BigDecimal getConversions(Boolean filter) {
    return BigDecimal.valueOf(Campaign.getInstance().getServerManager().fetchConversions(filter).size());
  }

  private BigDecimal getConversions(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return BigDecimal.valueOf(Campaign.getInstance().getServerManager().fetchConversions(impressionFilter, dateFilter).size());
  }

  /**
   * Gets the cost in pounds
   * @return BigDecimal The cost
   */
  private BigDecimal getCost(Boolean filter) {
    return BigDecimal.valueOf(getTotalCost(filter)/100d);
  }

  private BigDecimal getCost(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return BigDecimal.valueOf(getTotalCost(impressionFilter, dateFilter)/100d);
  }
}
