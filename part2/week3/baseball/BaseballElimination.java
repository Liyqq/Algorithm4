import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class BaseballElimination {
    private final HashMap<String, Integer> teamNameToIndex = new HashMap<>();
    private final String[] teamIndexToName;
    private final int[][] teamGameInfo;
    private final Bag<String>[] certificate;


    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int totalTeams = in.readInt(), n = totalTeams + 3;
        teamIndexToName = new String[totalTeams];
        teamGameInfo = new int[totalTeams][n];
        certificate = (Bag<String>[]) new Bag[totalTeams];
        for (int i = 0; i < totalTeams; i++)
            certificate[i] = new Bag<>();


        int teamCount = 0;
        while (!in.isEmpty()) {
            String teamName = in.readString();
            teamNameToIndex.put(teamName, teamCount);
            teamIndexToName[teamCount] = teamName;

            int[] gameInfo = teamGameInfo[teamCount++];
            for (int i = 0; i < n; i++)
                gameInfo[i] = in.readInt();
        }
        baseballElimination();
    }

    // number of teams
    public int numberOfTeams() {
        return teamIndexToName.length;
    }

    // all teams
    public Iterable<String> teams() {
        return teamNameToIndex.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        validTeam(team);
        int teamIndex = teamNameToIndex.get(team);
        return teamGameInfo[teamIndex][0];
    }

    // number of losses for given team
    public int losses(String team) {
        validTeam(team);
        int teamIndex = teamNameToIndex.get(team);
        return teamGameInfo[teamIndex][1];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validTeam(team);
        int teamIndex = teamNameToIndex.get(team);
        return teamGameInfo[teamIndex][2];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validTeam(team1);
        validTeam(team2);
        int team1Index = teamNameToIndex.get(team1), team2Index = teamNameToIndex.get(team2);
        return teamGameInfo[team1Index][3 + team2Index];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validTeam(team);
        int teamIndex = teamNameToIndex.get(team);
        return !certificate[teamIndex].isEmpty();
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validTeam(team);
        int teamIndex = teamNameToIndex.get(team);
        Bag<String> eliminationCertificate = certificate[teamIndex];
        return eliminationCertificate.isEmpty() ? null : eliminationCertificate;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    private void validTeam(String team) {
        if (!teamNameToIndex.containsKey(team))
            throw new IllegalArgumentException("the input arguments are invalid teams");
    }

    private void baseballElimination() {
        for (String team : teamNameToIndex.keySet()) {
            if (shortcutElimination(team)) continue;
            FlowNetwork fn = createFlowNetwork(team);
            int s = teamNameToIndex.get(team), t = fn.V() - 1;
            FordFulkerson ff = new FordFulkerson(fn, s, t);
            updateElimination(team, ff);
        }
    }

    private boolean shortcutElimination(String team) {
        int teamIndex = teamNameToIndex.get(team);
        int teamMaxPossibleWins = teamGameInfo[teamIndex][0] + teamGameInfo[teamIndex][2];
        for (String otherTeam : teamNameToIndex.keySet()) {
            if (team.equals(otherTeam)) continue;
            int otherTeamIndex = teamNameToIndex.get(otherTeam);
            int otherTeamWins = teamGameInfo[otherTeamIndex][0];
            if (teamMaxPossibleWins < otherTeamWins) {
                certificate[teamIndex].add(otherTeam);
                return true;
            }
        }
        return false;
    }

    private FlowNetwork createFlowNetwork(String team) {
        int teamIndex = teamNameToIndex.get(team);
        int teamMaxPossibleWins = teamGameInfo[teamIndex][0] + teamGameInfo[teamIndex][2];
        int totalTeams = numberOfTeams();

        int totalTeamVertices = totalTeams - 1;
        int totalGameVertices = (totalTeamVertices) * (totalTeamVertices - 1) / 2;
        int totalVertices = 2 + totalTeamVertices + totalGameVertices;

        // build FlowNetwork
        FlowNetwork fn = new FlowNetwork(totalVertices);
        int s = teamIndex, t = totalVertices - 1;
        int gameVerticesNo = totalTeams; // start with total team
        double capacityInf = Double.POSITIVE_INFINITY, capacity;
        for (int v = 0; v < totalTeams; v++) {
            if (v == s) continue; // from 0 to total teams ignore source vertex

            // add flow edge from team vertices to artificial target vertex
            int teamVWins = teamGameInfo[v][0];
            capacity = Math.max(teamMaxPossibleWins - teamVWins, 0.0);
            fn.addEdge(new FlowEdge(v, t, capacity, 0.0));

            // add flow edge from source vertex to game vertices
            // and from game vertices to team vertices
            for (int j = v + 1; j < totalTeams; j++) {
                if (j == s) continue; // ignore source vertex
                capacity = teamGameInfo[v][3 + j];
                fn.addEdge(new FlowEdge(s, gameVerticesNo, capacity, 0.0));

                fn.addEdge(new FlowEdge(gameVerticesNo, v, capacityInf, 0.0));
                fn.addEdge(new FlowEdge(gameVerticesNo, j, capacityInf, 0.0));
                gameVerticesNo++;
            }
        }
        return fn;
    }

    private void updateElimination(String team, FordFulkerson ff) {
        int teamIndex = teamNameToIndex.get(team);
        int n = teamIndexToName.length;
        for (int i = 0; i < n; i++) {
            if (i == teamIndex) continue;
            if (ff.inCut(i)) certificate[teamIndex].add(teamIndexToName[i]);
        }
    }
}
