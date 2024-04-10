/* Ma stratégie : gagner
 * 
 */
package welcome.ia;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import welcome.Jeu;
import welcome.Joueur;
import welcome.Travaux;
import welcome.Ville;
import welcome.Maison;
import welcome.Rue;
import welcome.utils.RandomSingleton;

public class Strat64 extends Strat{

    public Map<String, Double> weigths = new HashMap<String, Double>();
    /*
     * park1
     * park2
     * park3
     * pool
     * interim
     * bisneeded
     * bisnotneeded
     * lot1
     * lot2
     * lot3
     * lot4
     * lot5
     * lot6
     * place
     * plan
     */

    private Map<String, Double> decisionsScoreMap = new HashMap<String, Double>();
    
    public Strat64(){

    }
    
    @Override
    public String nomVille(){
        return "huuuuuuuuuuh";
    }
    
    @Override
    public String nomJoueur(){
        return "CHOUIPPE, Léo";
    }
    
    //Choisir au hasard parmi les 3 numéros dispos
    @Override
    public int choixCombinaison(Jeu j, int joueur){
        
        
        int res=-1;
        
        //A COMPLETER
        
        if(res<0 || res>2)
            res=RandomSingleton.getInstance().nextInt(3);
        return res;
    }
    
    //Choisir de placer un numéro bis
    @Override
    public int choixBis(Jeu j, int joueur, ArrayList<Integer> placeValide){
        int res=-1;
        
        //A COMPLETER
        
        if(res<0 || res>placeValide.size()-1)
            res=RandomSingleton.getInstance().nextInt(placeValide.size());
        return res;
    }
    
    //Choisir au hasard parmi les emplacements dispos
    @Override
    public int choixEmplacement(Jeu j, int joueur, int numero, ArrayList<Integer> placeValide){
        int res=-1;
        
        //A COMPLETER
        
        if(res<0 || res>placeValide.size()-1)
            res=RandomSingleton.getInstance().nextInt(placeValide.size());
        return res;
    }
    
    //Choisir le même numéro que celui de la carte quand l'action est un intérimaire
    @Override
    public int choixNumero(Jeu j, int joueur, int numero){
        int res=-1;
        
        //A COMPLETER
        
        if((res<(numero-2) || res>(numero+2)) || res<0)
            res=Math.max(0, RandomSingleton.getInstance().nextInt(5) + numero - 2) ;
        return res;
    }
    
    //Valorise aléatoirement une taille de lotissements (proba plus forte si plus d'avancements possibles)
    @Override
    public int valoriseLotissement(Jeu j, int joueur){        
        int res=-1;
        
        //A COMPLETER
        
        if(res<1 || res>6)
            res=RandomSingleton.getInstance().nextInt(6)+1;
        return res;
    }
    
    //Met une barrière à une position aléatoire
    @Override
    public int choixBarriere(Jeu j, int joueur,  ArrayList<Integer> placeValide){
        int res=-1;

        System.out.println(placeValide);
        
        //A COMPLETER
        
        if(res<0 || res>placeValide.size()-1)
            res=RandomSingleton.getInstance().nextInt(placeValide.size());
        return res;
    }
    
    @Override
    public boolean validePlan(Jeu j, int joueur, int plan) {
        boolean res = true;
        
        //Valide toujours un plan
        
        return res;
    }

    private ArrayList<Integer> construirePossibilite(int numero, Joueur joueur){
        int min; // Variable utiles
        ArrayList<Integer> possibilite= new ArrayList<Integer>(); //List des possibilités à construire
        for(int i=0; i<3; i++){//Pour chaque rue
            min=joueur.ville.rues[i].taille-1; //on part de la fin
            while(min>=0  && (joueur.ville.rues[i].maisons[min].numero==-1 || joueur.ville.rues[i].maisons[min].numero > numero))
                min--; // on décrement le min tant qu'on a pas trouvé un numéro <=
            if(min<0 || joueur.ville.rues[i].maisons[min].numero!=numero){

                min++;// On part de la case suivante
                while(min < joueur.ville.rues[i].taille && joueur.ville.rues[i].maisons[min].numero == -1){
                    possibilite.add((Integer)(min+ 100*i)); // on construit les possibilités tant qu'on a des cases vides
                    min++;
                }       
            }
        }
        return possibilite;
    }

    private Map<String, Integer> gradeCombination(Jeu j, int joueur) {
        String key = "";
        int score;

        int numero;
        String action;
        ArrayList<Integer> possibilities;

        //decisionsScoreMap

        for (int i=0; i<3; i++) {
            key = Integer.toString(i);

            //On récupère les combinaisons
            numero = ((Travaux) j.numeros[i].top()).getNumero();
            action = ((Travaux) j.actions[i].top()).getActionString(); //"Fabricant de piscine", "Agence d'intérim", "Numéro Bis\t", "Paysagiste\t", "Agent Immobilier", "Géomètre\t"

            possibilities = construirePossibilite(numero, j.joueurs[joueur]);

            for(int k=0; k<possibilities.size(); k++) {
                key += "," + Integer.toString(possibilities.get(k));
                switch (action) {
                    case "Fabricant de piscine":
                        if (isOnPool(j, joueur, possibilities.get(k))) {
                            decisionsScoreMap.put(key + ",pool", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)) + weigths.get("pool"));
                        } else {
                            decisionsScoreMap.put(key + ",nopool", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)));
                        }
                        break;

                    case "Agence d'intérim":
                        //TODO
                        break;

                    case "Numéro Bis\t":
                        //TODO
                        break;
                    
                    case "Paysagiste\t":
                        if (isParkFull(j, joueur, possibilities.get(k))) {
                            decisionsScoreMap.put(key + ",nopark", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)));
                        } else {
                            decisionsScoreMap.put(key + ",park", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)) + weigths.get("park" + Integer.toString(possibilities.get(k)/100 + 1)));
                        }
                        break;

                    case "Agent Immobilier":
                        for(int l=1; l<7; l++) { //TODO ajouter un if pour savoir si l'investissemenjt est plein
                            decisionsScoreMap.put(key + "," + Integer.toString(l), distanceToIdealPlace(j, joueur, numero, possibilities.get(k)) + weigths.get("lot" + Integer.toString(l)));
                        }
                        break;

                    case "Géomètre\t":
                        //TODO
                        break;

                    default: 
                        break;
                }
            }
        }

        return null;
    }

    //TODO takeDecisions();

    //renvoi les différence de lotissements par taille dans la rue en fonction du placement de la barrière
    private int[] lotissementChanged(Jeu j, int joueur, int place) {
        boolean[] barrieresList = j.joueurs[joueur].ville.barrieres[place/100];
        int[] nbLotissement = new int[6];

        int count = 0;
        for (int i=1; i<barrieresList.length; i++) {
            if (barrieresList[i]) {
                if (count<=5) {
                    nbLotissement[count]++;
                }
                count = 0;
            } else {
                count++;
            }
        }

        int[] newNbLotissement = new int[6];
        barrieresList[place%100] = true;

        count = 0;
        for (int i=1; i<barrieresList.length; i++) {
            if (barrieresList[i]) {
                if (count<=5) {
                    newNbLotissement[count]++;
                }
                count = 0;
            } else {
                count++;
            }
        }

        int[] result = new int[6];
        for (int i=0; i<result.length; i++) {
            result[i] = newNbLotissement[i] - nbLotissement[i];
        }
        return result;
    }

    private double distanceToIdealPlace(Jeu j, int joueur, int number, int place) {
        //On calcule la position du numero par rapport à la taille de la rue, et la position de la place ou on souhaite ajouter la maison par rapport à la taille de la rue 
        //le resultat est compris entre 0 et 1 (la plupart du temps pour les actions à réaliser)
        int position = place%100;
        int tailleRue = j.joueurs[joueur].ville.rues[place/100].taille;

        double idealRatio = (double) number/tailleRue;
        double realRatio = (double) position/tailleRue;

        return Math.sqrt(Math.pow((realRatio-idealRatio), 2));
    }

    private boolean isParkFull(Jeu j, int joueur, int place) {
        int indexRue = place/100;
        Ville ville = j.joueurs[joueur].ville;
        return (ville.nbParcs[indexRue]==ville.maxParcs[indexRue]);
    }

    private boolean isFillable(Jeu j, int joueur, int place) {
        int numRue = place/100;
        Rue rue = j.joueurs[joueur].ville.rues[numRue];
        Maison[] maisons = rue.maisons;

        int inf = 0;
        int sup = 17;
        boolean flag = false;
        int count = 0;

        for (int i=0; i<rue.taille; i++) {
            if (!maisons[i].estVide() && !flag) {
                inf = maisons[i].numero;
            } else if (!maisons[i].estVide() && flag && maisons[i].numero<sup) {
                sup = maisons[i].numero;
            } else if (maisons[i].estVide()) {
                count++;
            }
        }

        if (count+1<=sup-inf) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isOnPool(Jeu j, int joueur, int place) {
        return j.joueurs[joueur].ville.rues[place/100].maisons[place%100].emplacementPiscine;
    }
    
    @Override
    public void resetStrat(){};
}
