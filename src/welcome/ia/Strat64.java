/* Ma stratégie : gagner
 * 
 */
package welcome.ia;
import java.util.ArrayList;
import java.util.Arrays; 
import welcome.Jeu;
import welcome.Maison;
import welcome.Rue;
import welcome.utils.RandomSingleton;

public class Strat64 extends Strat{

    public int[] weigths = new int[15];
    
    public Strat64(){

    }
    
    @Override
    public String nomVille(){
        return "BidonVille";
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
        
        //A COMPLETER
        
        if(res<0 || res>placeValide.size()-1)
            res=RandomSingleton.getInstance().nextInt(placeValide.size());
        return res;
    }
    
    //Valide toujours un plan
    @Override
    public boolean validePlan(Jeu j, int joueur, int plan) {
        boolean res = true;
        
        //A COMPLETER
        
        return res;
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
    
    @Override
    public void resetStrat(){};
}
