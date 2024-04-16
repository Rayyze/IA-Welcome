package welcome;
import welcome.ia.*;
import welcome.utils.*;

import java.util.Map;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        exempleLanceIA();
        //exempleLanceJeuHumain();

        /* 
        Map<String, Double> trainResult = trainNoGroup(1000, 100, 10, 100, 0.5);

        for (Map.Entry<String, Double> entry : trainResult.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        */
    }
    
    public static void exempleLanceIA() {
        //On crée le jeu
        try{
            // recoit un tableau des numéros de strat (strat0 etc.).
            // tous ensemble, n parties avec TOUT LE MONDE
            TousEnsemble t = new TousEnsemble(new int[]{64}, 1000);
            
            // championnat1v1 : les joueurs s'affrontent en duel sur n parties pour chaque duel
            //Championnat1v1 t = new Championnat1v1(new int[]{0,0,0,0,0}, 10);
            
            t.run();
        }        
        catch(Exception e){
            e.printStackTrace();
            //System.out.println(e);
        }
    }
    
    public static void exempleLanceJeuHumain() {
        
        //On déclare des joeurs
        Joueur j1= new JoueurHumain("ShuterFly", "Poney Land");
        Joueur j2= new Bot(new Strat64(), "IA", "RobotVille");
        Joueur[] joueurs = new Joueur[1];
        joueurs[0]=j2;
        
        //On crée le jeu
        Jeu j= new Jeu(joueurs);
        try{
            
            int[] score = j.jouer(); //on lance la partie et on récupére les scores
            //Qu'on affiche
            for(int i=0; i<score.length; i++)
               System.out.println("Joueur " + i + " fini avec " + score[i] + " points.");
            
            //TousEnsemble t = new TousEnsemble(new int[]{1,2,1,2,1,2,1,2}, 100);
            //Championnat1v1 t = new Championnat1v1(new int[]{1,2,1,2,1,2,1,2}, 10);
            //t.run();
        }        
        catch(Exception e){
            e.printStackTrace();
            //System.out.println(e);
        }
    }
    
    public static Map<String, Double> trainNoGroup2(int nbGame, int nbGen, int selection, int nbInstance, double mutationRate) {

        Map<String, Double> tempWeights;
        Bot bot = new Bot(new Strat64(), "Léo", "huuuuuuh");
        @SuppressWarnings("unchecked")
        Map<String, Double>[] weightsArray = new Map[nbInstance];
        Joueur[] joueurs = new Joueur[1];
        joueurs[0] = bot;
        Jeu j = new Jeu(joueurs);
        double[] scoreInstances = new double[nbInstance];
        int[] singleInstanceScore = new int[nbGame];

        for (int i=0; i<nbInstance; i++) {
            weightsArray[i] = ((Strat64) bot.strat).getWeights();
        }

        for (int i=0; i<nbGen; i++) {
            System.out.println("Gen n°"+i);
            for (int l=0; l<nbInstance; l++) {
                j = new Jeu(joueurs);
                j.verboseOnOff(false);

                if (l!=0) {
                    tempWeights = new HashMap<String, Double>();
                    //tempWeights.putAll(((Strat64) botArray[l].strat).getWeights());
                    for (Map.Entry<String, Double> entry : tempWeights.entrySet()) {
                        tempWeights.put(entry.getKey(), entry.getValue() - mutationRate + 2*mutationRate*Math.random());
                    }
                    //((Strat64) botArray[l].strat).setWeights(tempWeights);
                }

                for(int k=0; k<nbGame; k++) {
                    int[] score = j.jouer();
                    j.reset();
                    j.verboseOnOff(false);
                    singleInstanceScore[k] = score[0];
                }

                int sum = 0;
                for (int num : singleInstanceScore) {
                    sum += num;
                }
                double average = (double) sum / singleInstanceScore.length;

                scoreInstances[l] = average;
            }

            //selection :
            if (i!=nbGen-1) {
                int maxIndex = getMaxIndex(scoreInstances);
                for (int l=0; l<nbInstance; l++) {
                    if (maxIndex!=l) {
                        bot = new Bot(new Strat64(), "IA", "RobotVille");
                        Map<String, Double> weightsCopy = new HashMap<String, Double>();
                        //weightsCopy.putAll(((Strat64) botArray[maxIndex].strat).getWeights());
                        ((Strat64) bot.strat).setWeights(weightsCopy);
                        //botArray[l] =  bot;
                    }
                }
            }
        }

        System.out.println(scoreInstances[getMaxIndices(scoreInstances, 1)[0]]);

        return weightsArray[getMaxIndices(scoreInstances, 1)[0]];
    }

    public static Map<String, Double> trainNoGroup(int nbGame, int nbGen, int selection, int nbInstance, double mutationRate) {
        Map<String, Double> tempWeights;
        Bot bot;
        Bot[] botArray = new Bot[nbInstance];
        Joueur[] joueurs;
        Jeu j; 
        double[] scoreInstances = new double[nbInstance];
        int[] singleInstanceScore = new int[nbGame];

        for (int i=0; i<nbInstance; i++) {
            bot = new Bot(new Strat64(), "IA", "RobotVille");
            botArray[i] = bot;
        }

        for (int i=0; i<nbGen; i++) {
            System.out.println("Gen n°"+i);
            for (int l=0; l<nbInstance; l++) {
                joueurs = new Joueur[1];
                joueurs[0] = botArray[l];
                j = new Jeu(joueurs);
                j.verboseOnOff(false);

                if (l!=0) {
                    tempWeights = new HashMap<String, Double>();
                    tempWeights.putAll(((Strat64) botArray[l].strat).getWeights());
                    for (Map.Entry<String, Double> entry : tempWeights.entrySet()) {
                        tempWeights.put(entry.getKey(), entry.getValue() - mutationRate + 2*mutationRate*Math.random());
                    }
                    ((Strat64) botArray[l].strat).setWeights(tempWeights);
                }

                for(int k=0; k<nbGame; k++) {
                    int[] score = j.jouer();
                    j.reset();
                    j.verboseOnOff(false);
                    singleInstanceScore[k] = score[0];
                }

                int sum = 0;
                for (int num : singleInstanceScore) {
                    sum += num;
                }
                double average = (double) sum / singleInstanceScore.length;

                scoreInstances[l] = average;
            }

            //selection :
            if (i!=nbGen-1) {
                int maxIndex = getMaxIndex(scoreInstances);
                for (int l=0; l<nbInstance; l++) {
                    if (maxIndex!=l) {
                        bot = new Bot(new Strat64(), "IA", "RobotVille");
                        Map<String, Double> weightsCopy = new HashMap<String, Double>();
                        weightsCopy.putAll(((Strat64) botArray[maxIndex].strat).getWeights());
                        ((Strat64) bot.strat).setWeights(weightsCopy);
                        botArray[l] =  bot;
                    }
                }
            }
        }

        System.out.println(scoreInstances[getMaxIndices(scoreInstances, 1)[0]]);

        return ((Strat64) botArray[getMaxIndices(scoreInstances, 1)[0]].strat).getWeights();
    }

    public static int getMaxIndex(double[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array must not be null or empty");
        }

        int maxIndex = 0;  // Assume the first element is the maximum
        double maxValue = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public static int[] getMaxIndices(double[] array, int n) {
    
        int[] indices = new int[n];
        double tempMax;
        int tempInd;
        boolean[] used = new boolean[array.length];
        for (int i=0; i<array.length; i++) {
            used[i] = false;
        }
        double[] maxima = new double[n];
        
        for (int j=0; j<n; j++) {
            tempMax = 0.0;
            tempInd = -1;
            for (int i=0; i<array.length; i++) {
                if (!used[i] && tempMax<array[i]) {
                    tempMax = array[i];
                    used[i] = true;
                    tempInd = i;
                }
            }
            maxima[j] = tempMax;
            indices[j] = tempInd;
        }
    
        return indices;
    }
    

    public static boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }
}
