/* Ma stratégie : gagner
 * 
 */
package welcome.ia;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import welcome.Jeu;
import welcome.Joueur;
import welcome.Lotissement;
import welcome.Travaux;
import welcome.Ville;
import welcome.Maison;
import welcome.Rue;
import welcome.utils.RandomSingleton;

public class Strat64 extends Strat{

    public Map<String, Double> weights = new HashMap<String, Double>();
    /*
     * park1
     * park2
     * park3
     * pool
     * interim
     * interimneeded
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

    public Map<String, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<String, Double> inputWeights) { //TODO mettre en private avant d'envoyer
        for (Map.Entry<String, Double> entry : inputWeights.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            weights.put(key, value);
        }
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

    //Construction des possibilités de placement des numéros bis
    private ArrayList<Integer> constuireBis(Joueur joueur){
        ArrayList<Integer> possibilite= new ArrayList<Integer>();
        possibilite.add(1000);
        for(int i=0; i<3; i++){ //on parcours les rues
            for(int j=0; j<joueur.ville.rues[i].taille; j++){ //on parcours les maisons
                if(joueur.ville.rues[i].maisons[j].estVide()){ //Pour chaque maison vide
                    if(j>0 && !joueur.ville.rues[i].maisons[j-1].estVide() && !joueur.ville.barrieres[i][j]){ //On check le numero à gauche
                        possibilite.add(-1 * i * 100 - j);
                    }
                    if(j<joueur.ville.rues[i].taille-1 && !joueur.ville.rues[i].maisons[j+1].estVide() && !joueur.ville.barrieres[i][j+1]){//On check le numero à droite
                        possibilite.add(i * 100 + j);
                    }
                }
            }
        }
        return possibilite;
    }

    //Construction des possibilités pour les choix de barrières
    private ArrayList<Integer> construireChoixPlacementBarriere(Joueur joueur) {
        Lotissement l;
        //Les possibilités seront ajoutés dans une liste
        //Une possibilité sera représenté par un entier ayant la valeur suivante: 100*n°rue + position dans la rue
        //Création de la liste
        ArrayList<Integer> possibilite= new ArrayList<Integer>();
        possibilite.add(0);
        //On parcours les lotissements
        for(int i=0; i<joueur.ville.lotissements.size(); i++){
            l=joueur.ville.lotissements.get(i);
            if(l.dispo){//Si le lotissement est dispo (pas encore utilisé pour la validation d'un objectif)
                for(int j=l.debut+1; j<l.fin; j++){ //on ajoute aux possibilités les barrières entre les 2 barrières du lotissements   
                    if(joueur.ville.rues[l.rue.numero-1].maisons[j-1].numero <0 || joueur.ville.rues[l.rue.numero-1].maisons[j-1].numero != joueur.ville.rues[l.rue.numero-1].maisons[j].numero){
                        possibilite.add((Integer)((l.rue.numero-1) * 100 + j));
                    }
                }
            }
        }
        return possibilite;
    }

    private Map<String, Integer> gradeCombination(Jeu j, int joueur) {
        String key = "";

        ArrayList<ArrayList<Integer>> numerosInterim = new ArrayList<ArrayList<Integer>>();

        int numero;
        String action;
        ArrayList<Integer> possibilities;

        for (int i=0; i<3; i++) {
            key = Integer.toString(i);

            numerosInterim.add(new ArrayList<Integer>());

            //On récupère les combinaisons
            numero = ((Travaux) j.numeros[i].top()).getNumero();
            action = ((Travaux) j.actions[i].top()).getActionString(); //"Fabricant de piscine", "Agence d'intérim", "Numéro Bis\t", "Paysagiste\t", "Agent Immobilier", "Géomètre\t"

            possibilities = construirePossibilite(numero, j.joueurs[joueur]);

            for(int k=0; k<possibilities.size(); k++) {
                key += "," + Integer.toString(possibilities.get(k));
                switch (action) {
                    case "Fabricant de piscine":
                        if (isOnPool(j, joueur, possibilities.get(k))) {
                            decisionsScoreMap.put(key + ",pool", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)) + weights.get("pool"));
                        } else {
                            decisionsScoreMap.put(key + ",nopool", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)));
                        }
                        break;

                    case "Agence d'intérim":
                        if(isInterimNeeded(j, joueur, possibilities.get(k))) {
                            decisionsScoreMap.put(key + ",0" + "interim", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)) + weights.get("interimneeded"));
                        } else {
                            decisionsScoreMap.put(key + ",0" + "interim", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)) + weights.get("interim"));
                        }
                        for (int l = -2; l<=2; l++) {
                            if (numero+l>=0) {
                                numerosInterim.get(i).add(numero+l);
                            }
                        }
                        break;

                    case "Numéro Bis\t":
                        //TODO parcourir les possibilités de placement de bis
                        break;
                    
                    case "Paysagiste\t":
                        if (isParkFull(j, joueur, possibilities.get(k))) {
                            decisionsScoreMap.put(key + ",nopark", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)));
                        } else {
                            decisionsScoreMap.put(key + ",park", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)) + weights.get("park" + Integer.toString(possibilities.get(k)/100 + 1)));
                        }
                        break;

                    case "Agent Immobilier":
                        for(int l=1; l<7; l++) {
                            if(j.joueurs[joueur].ville.avancementPrixLotissement[l-1]==j.joueurs[joueur].ville.maxAvancement[l-1]) {
                                decisionsScoreMap.put(key + "," + Integer.toString(l) + "full", distanceToIdealPlace(j, joueur, numero, possibilities.get(k)));
                            } else {
                                decisionsScoreMap.put(key + "," + Integer.toString(l), distanceToIdealPlace(j, joueur, numero, possibilities.get(k)) + weights.get("lot" + Integer.toString(l)));
                            }
                        }
                        break;

                    case "Géomètre\t":
                        //TODO parcourir les possibilités de placement de barrières
                        break;

                    default: 
                        break;
                }
            }
        }

        for (int i=0; i<3; i++) {
            key = Integer.toString(i);
            for (int k=0; k<numerosInterim.get(i).size(); k++){
                ArrayList<Integer> interimPossibilities = construirePossibilite(numerosInterim.get(i).get(k), j.joueurs[joueur]);

                for (int l=0; l<interimPossibilities.size(); l++) {
                    if(isInterimNeeded(j, joueur, interimPossibilities.get(l))) {
                        decisionsScoreMap.put(key + "," + Integer.toString(l) + "interim", distanceToIdealPlace(j, joueur, numerosInterim.get(i).get(k), interimPossibilities.get(l)) + weights.get("interimneeded"));
                    } else {
                        decisionsScoreMap.put(key + "," + Integer.toString(l) + "interim", distanceToIdealPlace(j, joueur, numerosInterim.get(i).get(k), interimPossibilities.get(l)) + weights.get("interim"));
                    }
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

    private boolean isBisNeeded(Jeu j, int joueur, int place) {
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

    private boolean isInterimNeeded(Jeu j, int joueur, int place) {
        int numRue = place/100;
        Rue rue = j.joueurs[joueur].ville.rues[numRue];
        Maison[] maisons = rue.maisons;

        int indFirst = -1;
        int indLast = -1;

        for (int i=0; i<rue.taille; i++) {
            if (!maisons[i].estVide()) {
                if (indFirst==-1) {
                    indFirst = i;
                }
                if (indLast<i) {
                    indLast = 1;
                }
            }
        }

        if ((place%100<indFirst && maisons[indFirst].numero==1) || (place%100>indLast && (maisons[indLast].numero==15 || maisons[indLast].numero==16))) {
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
