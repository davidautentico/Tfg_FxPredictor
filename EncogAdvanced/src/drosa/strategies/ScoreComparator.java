package drosa.strategies;

import java.util.Comparator;

public class ScoreComparator implements Comparator<ParametersFS>{
 
    @Override
    public int compare(ParametersFS p1, ParametersFS p2) {
        return (p1.score>p2.score ? -1 : (p1.score==p2.score ? 0 : 1));
    }

}
