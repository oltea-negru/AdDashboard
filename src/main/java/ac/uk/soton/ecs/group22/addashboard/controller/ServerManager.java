package ac.uk.soton.ecs.group22.addashboard.controller;

import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickLoader;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerLoader;
import ac.uk.soton.ecs.group22.addashboard.events.LoadDataEvent;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * <h1> Server Manager </h1>
 * The controller class for the server log, provides a bridge between the GUI and the data
 *
 * @version 1.0
 * @see AbstractManager
 * @since 2022-03-07
 */

public class ServerManager extends AbstractManager {
  private final Map<Long, Set<ServerEntry>> server;

  /**
   * Constructor, initialises the server map
   */
  public ServerManager() {
    this.server = new LinkedHashMap<>();

    LoadDataEvent.getListeners().add(loadDataEvent -> hashedFilter = Integer.MIN_VALUE);
  }

  public List<ServerEntry> getServer() {
    return server.values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }
  /**
   * Creates and returns a ServerLoader for the given file
   *
   * @param file the given file
   * @return ServerLoader a ServerLoader for the given file
   * @see ServerLoader
   */
  public ServerLoader createLoader(File file) {
    return new ServerLoader(file);
  }

  /**
   * Clears and updates the server entry
   *
   * @param loader the loader to get the data from
   * @see ServerLoader
   */
  public void setServer(ServerLoader loader) {
    this.server.clear();
    for (ServerEntry serverEntry : loader.getServer()) {
      Set<ServerEntry> serverEntrySet = server.computeIfAbsent(serverEntry.getId(), k -> new HashSet<>());
      serverEntrySet.add(serverEntry);
    }
    setHasLoadedInData(true);
  }

  private List<ServerEntry> matches;
  private int hashedFilter = -1;

  /**
   * Returns the list of server entries that pass the filter
   *
   * @return List<ServerEntry> The filtered server entries
   */
  public List<ServerEntry> fetchMatches() {
    int newHash = Campaign.getInstance().getImpressionFilter().hashCode() + Campaign.getInstance().getDateFilter().hashCode();

    if (hashedFilter == newHash) {
      return matches;
    }

    List<ImpressionEntry> impressions = Campaign.getInstance().getImpressionManager().fetchMatches();

    matches =  impressions.stream()
        .filter(impression -> server.containsKey(impression.getId()))
        .flatMap(impression -> server.get(impression.getId()).stream())
        .filter(server -> Campaign.getInstance().getDateFilter().matches(server.getEntryDate()))
        .collect(Collectors.toList());
    hashedFilter = newHash;

    return matches;
  }

  public List<ServerEntry> fetchMatches(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    List<ImpressionEntry> impressions = Campaign.getInstance().getImpressionManager().fetchMatches(impressionFilter, dateFilter);
    return impressions.stream()
        .filter(impression -> server.containsKey(impression.getId()))
        .flatMap(impression -> server.get(impression.getId()).stream())
        .filter(server -> dateFilter.matches(server.getEntryDate()))
        .collect(Collectors.toList());
  }

  public List<ServerEntry> fetchBounces() {
    return fetchMatches().stream()
        .filter(e -> (e.getExitDate() - e.getEntryDate()) <= Settings.getInstance().getTimeForBounce())
        .filter(e -> e.getPagesViewed() <= Settings.getInstance().getPagesForBounce())
        .toList();
  }

  public List<ServerEntry> fetchBounces(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return fetchMatches(impressionFilter, dateFilter).stream()
        .filter(e -> (e.getExitDate() - e.getEntryDate()) <= Settings.getInstance().getTimeForBounce())
        .filter(e -> e.getPagesViewed() <= Settings.getInstance().getPagesForBounce())
        .toList();
  }

  public List<ServerEntry> fetchConversions(Boolean filter) {
    return !filter? getServer().stream().filter(ServerEntry::isConversion).toList(): fetchMatches().stream()
        .filter(ServerEntry::isConversion)
        .toList();
  }

  public List<ServerEntry> fetchConversions(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    return fetchMatches(impressionFilter, dateFilter).stream()
        .filter(ServerEntry::isConversion)
        .toList();
  }

  /**
   * Clears the server entries
   */
  public void clear() {
    server.clear();
    setHasLoadedInData(false);
  }

}
