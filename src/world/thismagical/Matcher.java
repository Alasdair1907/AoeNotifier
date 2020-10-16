package world.thismagical;

import world.thismagical.filters.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Matcher {
    public static List<Lobby> getMatchingLobbies(List<FiltersContainer> filtersContainers, List<Lobby> lobbies){

        List<Lobby> matchingLobbies = new ArrayList<>();

        for (Lobby lobby : lobbies){
            try {
                if (containersMatchLobby(filtersContainers, lobby)) {
                    matchingLobbies.add(lobby);
                }
            } catch (Exception ex){
                System.out.println("Error matching lobby");
            }
        }

        return matchingLobbies;
    }

    public static Boolean containersMatchLobby(List<FiltersContainer> filtersContainers, Lobby lobby){
        for (FiltersContainer filtersContainer : filtersContainers){
            if (containerMatchesLobby(filtersContainer, lobby)){
                return true;
            }
        }
        return false;
    }

    public static Boolean containerMatchesLobby(FiltersContainer filtersContainer, Lobby lobby){

        for (Filter filter : filtersContainer.getFilters()){
            if (!filterMatchesLobby(filter, lobby)){
                return false;
            }
        }

        return true;
    }

    public static Boolean filterMatchesLobby(Filter filter, Lobby lobby){

        if (filter.getFilterType() == FilterType.LOBBY_TITLE){
            return stringsMatch(filter.getPredicateType(), filter.getTextMode(), filter.getPredicate(), lobby.getTitle());
        } else if (filter.getFilterType() == FilterType.PLAYER_NAME){
            List<String> playerNames = lobby.getPlayers().stream().map(Player::getName).collect(Collectors.toList());
            return listOfStringsContains(filter.getPredicateType(), filter.getTextMode(), filter.getPredicate(), playerNames);
        } else {
            throw new IllegalArgumentException("UNKNOWN FILTER TYPE");
        }
    }

    public static Boolean listOfStringsContains(PredicateType predicateType, TextMode textMode, String predicate, List<String> texts){

        if (texts == null || texts.isEmpty()){
            return false;
        }

        for (String text : texts){
            if (stringsMatch(predicateType, textMode, predicate, text)){
                return true;
            }
        }
        return false;
    }

    public static Boolean stringsMatch(PredicateType predicateType, TextMode textMode, String predicate, String text){

        if (predicate == null || predicate.isBlank()){
            throw new IllegalArgumentException("PREDICATE IS EMPTY");
        }
        if (text == null || text.isBlank()){
            return false;
        }

        Boolean caseSensitive = textMode == TextMode.CASE_SENSITIVE;

        if (predicateType == PredicateType.FULL_MATCH){
            return stringsMatch(caseSensitive, predicate, text);
        } else if (predicateType == PredicateType.CONTAINS){
            return stringContainsWord(caseSensitive, predicate, text);
        } else if (predicateType == PredicateType.ALL_WORDS){
            return stringContainsWords(caseSensitive, predicate, text);
        } else {
            throw new IllegalArgumentException("UNKNOWN PREDICATE TYPE");
        }
    }


    public static Boolean stringsMatch(Boolean caseSensitive, String a, String b){
        if (!caseSensitive){
            a = a.toLowerCase();
            b = b.toLowerCase();
        }

        return a.equals(b);
    }

    public static Boolean stringContainsWord(Boolean caseSensitive, String predicate, String text){
        if (!caseSensitive){
            text = text.toLowerCase();
            predicate = predicate.toLowerCase();
        }

        return text.contains(predicate);
    }

    public static Boolean stringContainsWords(Boolean caseSensitive, String predicate, String text){
        List<String> words = Arrays.stream(predicate.split("\\s")).collect(Collectors.toList());

        if (!caseSensitive){
            words = words.stream().map(String::toLowerCase).collect(Collectors.toList());
            text = text.toLowerCase();
        }

        for (String word : words){
            if (!text.contains(word)){
                return false;
            }
        }

        return true;
    }
}
