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
import welcome.Plan;
import welcome.Rue;

public class Strat64 extends Strat{

    public Map<String, Double> weights = new HashMap<String, Double>();
    /*
     * park1 : valorise les parks de la rue 1
     * park2 : valorise les parks de la rue 2
     * park3 : valorise les parks de la rue 3
     * pool : si la piscine est sur un emplacement piscine
     * interim si l'interim n'est pas nécessaire
     * interimneeded : si l'interim est nécessaire
     * bisneeded : si le bis est nécessaire
     * bisnotneeded : si le bis n'est pas nécessaire
     * lot1 : valorise les lotissement de 1
     * lot2 : valorise les lotissement de 2
     * lot3 : valorise les lotissement de 3
     * lot4 : valorise les lotissement de 4
     * lot5 : valorise les lotissement de 5
     * lot6 : valorise les lotissement de 6
     * place : emplacement choisi
     * plan : les plans
     * numrar : rareté du nombre
     */

    private int[] drawnNumbers = new int[18];
    private int[] totalNumbers = new int[]{0,3,3,4,5,6,7,8,9,8,7,6,5,4,3,3,0,0};
    private int tour = 0;

    private Map<String, Double> decisionsScoreMap = new HashMap<String, Double>();
    private int choixCombinaisonResult = 0;
    private int choixEmplacementResult = 0;
    private int choixNumeroResult = 0;
    private int choixBisResult = 0;
    private int choixBarriereResult = 0;
    private int valoriseLotissementResult = 0;
    
    public Strat64(){
        Map<String, Double> initialWeights = new HashMap<String, Double>();
        initialWeights.put("park1", 7.129482883637951);
        initialWeights.put("park2", 2.3228956853445775);
        initialWeights.put("park3", 3.768497288773496);
        initialWeights.put("pool", 0.634140649275078);
        initialWeights.put("interim", -1.4162862869886308);
        initialWeights.put("interimneeded", 2.2897171988786384);
        initialWeights.put("bisneeded", -2.663308682791608);
        initialWeights.put("bisnotneeded", -4.701958345341477);
        initialWeights.put("lot1", -1.4469854143185723);
        initialWeights.put("lot2", -1.1171254059511642);
        initialWeights.put("lot3", -2.182323245996866);
        initialWeights.put("lot4", 0.24961986541084624);
        initialWeights.put("lot5", -2.273567746851794);
        initialWeights.put("lot6", -2.9609902855729286);
        initialWeights.put("place", 7.493868932785887);
        initialWeights.put("plan", 2.1458941319774656);
        initialWeights.put("numrar", 1.0);
        setWeights(initialWeights);
    }

    public Map<String, Double> getWeights() {
        Map<String, Double> weightsCopy = new HashMap<String, Double>();
        weightsCopy.putAll(weights);
        return weightsCopy;
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
    
    @Override
    public int choixCombinaison(Jeu j, int joueur){
        decisionsScoreMap = new HashMap<String, Double>();
        tour++;
        gradeCombination(j, joueur);
        takeDecision(j);
        updateDrawnNumber(j);
        if(tour==27){
            drawnNumbers = new int[18];
        }
        return choixCombinaisonResult;
    }
    
    @Override
    public int choixBis(Jeu j, int joueur, ArrayList<Integer> placeValide){
        return placeValide.indexOf(choixBisResult);
    }
    
    @Override
    public int choixEmplacement(Jeu j, int joueur, int numero, ArrayList<Integer> placeValide){
        return placeValide.indexOf(choixEmplacementResult);
    }
    
    @Override
    public int choixNumero(Jeu j, int joueur, int numero){
        return choixNumeroResult;
    }
    
    @Override
    public int valoriseLotissement(Jeu j, int joueur){      
        return valoriseLotissementResult;
    }
    
    //Met une barrière à une position aléatoire
    @Override
    public int choixBarriere(Jeu j, int joueur,  ArrayList<Integer> placeValide){
        return placeValide.indexOf(choixBarriereResult);
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

    private void gradeCombination(Jeu j, int joueur) {
        String key = "";

        ArrayList<ArrayList<Integer>> numerosInterim = new ArrayList<ArrayList<Integer>>();

        int numero;
        String action;
        ArrayList<Integer> possibilities;

        for (int i=0; i<3; i++) {

            numerosInterim.add(new ArrayList<Integer>());

            //On récupère les combinaisons
            numero = ((Travaux) j.numeros[i].top()).getNumero();
            action = ((Travaux) j.actions[i].top()).getActionString(); //"Fabricant de piscine", "Agence d'intérim", "Numéro Bis\t", "Paysagiste\t", "Agent Immobilier", "Géomètre\t"

            if (action == "Agence d'intérim") {
                for (int l = -2; l<=2; l++) {
                    if (numero+l>=0) {
                        numerosInterim.get(i).add(numero+l);
                    }
                }
            }

            possibilities = construirePossibilite(numero, j.joueurs[joueur]);

            for(int k=0; k<possibilities.size(); k++) {
                key = Integer.toString(i) + ";" + Integer.toString(possibilities.get(k));
                switch (action) {
                    case "Fabricant de piscine":
                        if (isOnPool(j, joueur, possibilities.get(k))) {
                            decisionsScoreMap.put(key + ";pool;0", -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + weights.get("pool") + numberRarity(numero)*weights.get("numrar"));
                        } else {
                            decisionsScoreMap.put(key + ";pool;0", -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + numberRarity(numero)*weights.get("numrar"));
                        }
                        break;

                    case "Numéro Bis\t":
                        ArrayList<Integer> bisPossibilities = constuireBis(j.joueurs[joueur]);
                        for (int l=0; l<bisPossibilities.size(); l++) {
                            if (Math.abs(bisPossibilities.get(l))!=possibilities.get(k))
                                if(isBisNeeded(j, joueur, Math.abs(bisPossibilities.get(l)))) {
                                    decisionsScoreMap.put(key + ";bis;" + Integer.toString(bisPossibilities.get(l)), -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + weights.get("bisneeded") + numberRarity(numero)*weights.get("numrar"));
                                } else {
                                    decisionsScoreMap.put(key + ";bis;" + Integer.toString(bisPossibilities.get(l)), -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + weights.get("bisnotneeded") + numberRarity(numero)*weights.get("numrar"));
                                }
                        }
                        decisionsScoreMap.put(key + ";bis;1000" , -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + numberRarity(numero)*weights.get("numrar"));
                        break;
                    
                    case "Paysagiste\t":
                        if (isParkFull(j, joueur, possibilities.get(k))) {
                            decisionsScoreMap.put(key + ";nopark;0", -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + numberRarity(numero)*weights.get("numrar"));
                        } else {
                            decisionsScoreMap.put(key + ";park;0", -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + weights.get("park" + Integer.toString(possibilities.get(k)/100 + 1)) + numberRarity(numero)*weights.get("numrar"));
                        }
                        break;

                    case "Agent Immobilier":
                        for(int l=1; l<7; l++) {
                            if(j.joueurs[joueur].ville.avancementPrixLotissement[l-1]==j.joueurs[joueur].ville.maxAvancement[l-1]) {
                                decisionsScoreMap.put(key + ";invest;1", -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + numberRarity(numero)*weights.get("numrar"));
                            } else {
                                decisionsScoreMap.put(key + ";invest;" + Integer.toString(l), -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + weights.get("lot" + Integer.toString(l)) + numberRarity(numero)*weights.get("numrar"));
                            }
                        }
                        break;

                    case "Géomètre\t":
                        ArrayList<Integer> barrierPossibilities = construireChoixPlacementBarriere(j.joueurs[joueur]);
                        for (int l=0; l<barrierPossibilities.size(); l++) {
                            int[] changedLot = lotissementChanged(j, joueur, barrierPossibilities.get(l));
                            double lotImpact = 0.0;
                            for (int m=0; m<6; m++) {
                                lotImpact += changedLot[m]*weights.get("lot" + Integer.toString(m+1));
                            }
                            if(isFollowingPlan(j, joueur, barrierPossibilities.get(l))) {
                                decisionsScoreMap.put(key + ";barrier;" + Integer.toString(barrierPossibilities.get(l)), -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + lotImpact + weights.get("plan") + numberRarity(numero)*weights.get("numrar"));
                            } else {
                                decisionsScoreMap.put(key + ";barrier;" + Integer.toString(barrierPossibilities.get(l)), -distanceToIdealPlace(j, joueur, numero, possibilities.get(k))*weights.get("place") + lotImpact + numberRarity(numero)*weights.get("numrar"));
                            }
                        }
                        break;

                    default: 
                        break;
                }
            }
        }

        for (int i=0; i<3; i++) {
            for (int k=0; k<numerosInterim.get(i).size(); k++){
                ArrayList<Integer> interimPossibilities = construirePossibilite(numerosInterim.get(i).get(k), j.joueurs[joueur]);

                for (int l=0; l<interimPossibilities.size(); l++) {
                    key = Integer.toString(i) + ";" + Integer.toString(interimPossibilities.get(l));
                    if(isInterimNeeded(j, joueur, interimPossibilities.get(l))) {
                        decisionsScoreMap.put(key + ";interim;" + Integer.toString(numerosInterim.get(i).get(k)), -distanceToIdealPlace(j, joueur, numerosInterim.get(i).get(k), interimPossibilities.get(l))*weights.get("place") + weights.get("interimneeded"));
                    } else {
                        decisionsScoreMap.put(key + ";interim;" + Integer.toString(numerosInterim.get(i).get(k)), -distanceToIdealPlace(j, joueur, numerosInterim.get(i).get(k), interimPossibilities.get(l))*weights.get("place") + weights.get("interim"));
                    }
                }
            }
        }
    }

    private void takeDecision(Jeu j) {
        double maxValue = Double.NEGATIVE_INFINITY;
        String maxKey = null;

        for (Map.Entry<String, Double> entry : decisionsScoreMap.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue();
            
            if (value > maxValue) {
                maxValue = value;
                maxKey = key;
            }
        }

        if (!decisionsScoreMap.isEmpty()) {
            String[] decisions = maxKey.split(";");

            choixCombinaisonResult = Integer.valueOf(decisions[0]);
            choixEmplacementResult = Integer.valueOf(decisions[1]);

            switch (decisions[2]) {
                case "interim":
                    choixNumeroResult = Integer.valueOf(decisions[3]);
                    break;
                
                case "invest":
                    valoriseLotissementResult = Integer.valueOf(decisions[3]);
                    break;
                
                case "bis":
                    choixBisResult = Integer.valueOf(decisions[3]);
                    break;
                
                case "barrier":
                    choixBarriereResult = Integer.valueOf(decisions[3]);
                    break;
                    
                default:
                    break;
            } 
        } else {
            int numero = ((Travaux) j.numeros[choixCombinaisonResult].top()).getNumero();
            choixNumeroResult = numero;
        }
    }

    //renvoi les différence de lotissements par taille dans la rue en fonction du placement de la barrière
    private int[] lotissementChanged(Jeu j, int joueur, int place) {
        boolean[] barrieresList = j.joueurs[joueur].ville.barrieres[place/100];
        int[] nbLotissement = lotNumber(barrieresList);
        barrieresList[place%100] = true;
        int[] newNbLotissement = lotNumber(barrieresList);

        int[] result = new int[6];
        for (int i=0; i<result.length; i++) {
            result[i] = newNbLotissement[i] - nbLotissement[i];
        }
        return result;
    }

    private int[] lotNumber(boolean[] barrieresList) {
        int[] nbLotissement = new int[] {0,0,0,0,0,0};
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

        return nbLotissement;
    }

    private boolean isFollowingPlan(Jeu j, int joueur, int place) {
        Plan[] plansJeu = j.plans;
        int[] lotNeededNb = new int[] {0,0,0,0,0,0};
        int[] myLots = new int[] {0,0,0,0,0,0};
        int[] changedLot = lotissementChanged(j, joueur, place);

        int following = 0;

        for (int i=0; i<3; i++) {
            if (j.joueurs[joueur].objectifs[i]==0){
                for (int k=0; k<plansJeu[i].nbLotissement; k++) { 
                    lotNeededNb[plansJeu[i].tailleLotissements[k]-1]++;
                }
            }
        }

        for (Lotissement lotissement : j.joueurs[joueur].ville.lotissements) {
            if(lotissement.taille<7 && lotissement.dispo){
                myLots[lotissement.taille-1]++;
            }
        }



        for (int i=0; i<6; i++) {
            if (lotNeededNb[i]>myLots[i] && changedLot[i]>0) {
                following++;
            } else if (lotNeededNb[i]>myLots[i] && changedLot[i]<0) {
                following--;
            }
        }

        if (following>0) {
            return true;
        } else {
            return false;
        }
    }

    private double distanceToIdealPlace(Jeu j, int joueur, int number, int place) {
        //On calcule la position du numero par rapport à la taille de la rue, et la position de la place ou on souhaite ajouter la maison par rapport à la taille de la rue 
        //le resultat est compris entre 0 et 1 (la plupart du temps pour les actions à réaliser)
        int position = place%100;
        int tailleRue = j.joueurs[joueur].ville.rues[place/100].taille;

        double idealRatio = (double) number/15;
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
        Rue rue = j.joueurs[joueur].ville.rues[numRue]; //java.lang.ArrayIndexOutOfBoundsException: Index 10 out of bounds for length 3
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

        int indFirst = 0;
        int indLast = rue.taille-1;

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

    private double numberRarity(int number) {
        return 1/(totalNumbers[number]-drawnNumbers[number]+1);
    }

    private void updateDrawnNumber(Jeu j) {
        int numero;
        for (int i=0; i<3; i++) {
            numero = ((Travaux) j.numeros[i].top()).getNumero();
            drawnNumbers[numero]++;
        }
    }
    
    @Override
    public void resetStrat(){
        drawnNumbers = new int[18];
        tour = 0;
    };
}
