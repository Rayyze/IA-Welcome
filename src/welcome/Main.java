package welcome;
import welcome.ia.*;
import welcome.utils.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        exempleLanceIA();
        //exempleLanceJeuHumain();

        /*
        Map<String, Double> trainResult = trainNoGroup(1000, 10, 10, 100, 0.5);

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
    
    public static Map<String, Double> trainNoGroup(int nbGame, int nbGen, int selection, int nbInstance, double mutationRate) {
        Map<String, Double> result = new HashMap<String,Double>();
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
            for (int l=0; l<nbInstance; l++) {
                joueurs = new Joueur[1];
                joueurs[0] = botArray[l];
                j = new Jeu(joueurs);

                if (l!=0) {
                    tempWeights = ((Strat64) botArray[l].strat).getWeights();
                    for (Map.Entry<String, Double> entry : tempWeights.entrySet()) {
                        tempWeights.put(entry.getKey(), entry.getValue() - mutationRate + 2*mutationRate*Math.random());
                    }
                    ((Strat64) botArray[l].strat).setWeights(tempWeights);
                }

                for(int k=0; k<nbGame; k++) {
                    int[] score = j.jouer();
                    singleInstanceScore[k] = score[0];
                }

                int sum = 0;
                for (int num : singleInstanceScore) {
                    sum += num;
                }
                double average = (double) sum / singleInstanceScore.length;

                scoreInstances[l] = average;
            }

            int[] maxIndices = getMaxIndices(scoreInstances, 10);
            for (int l=0; l<nbInstance; l++) {
                if (!contains(maxIndices, l)) {
                    bot = new Bot(new Strat64(), "IA", "RobotVille");
                    ((Strat64) bot.strat).setWeights(((Strat64) botArray[maxIndices[l%selection]].strat).getWeights());
                    botArray[l] =  bot;
                }
            }
        }

        return ((Strat64) botArray[getMaxIndices(scoreInstances, 1)[0]].strat).getWeights();
    }

    public static int[] getMaxIndices(double[] array, int n) {
        int[] indices = new int[n];
        double[] copyArray = Arrays.copyOf(array, array.length);
        Arrays.sort(copyArray);
        for (int i = 0; i < n; i++) {
            double max = copyArray[copyArray.length - 1 - i];
            for (int j = 0; j < array.length; j++) {
                if (array[j] == max) {
                    indices[i] = j;
                    break;
                }
            }
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
