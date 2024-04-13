package welcome;
import welcome.ia.*;
import welcome.utils.*;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        exempleLanceIA();
        //exempleLanceJeuHumain();
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
    
    public static Map<String, Double> trainNoGroup(int NbGame, int nbGen, double mutationRate) {
        Map<String, Double> result = new HashMap<String,Double>();

        for (int i=0; i<nbGen; i++) {
            for(int k=0; k<NbGame; k++) {

            }
        }

        return result;
    }
    //TODO écrire la methode train
    
}
