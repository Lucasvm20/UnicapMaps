package unicap.grafos.unicapmaps.AlgoritmosGrafo;

import java.util.ArrayList;
import java.util.Collections;

import unicap.grafos.unicapmaps.controller.GrafoController;
import unicap.grafos.unicapmaps.model.Aresta;
import unicap.grafos.unicapmaps.model.Vertice;

import static java.lang.StrictMath.abs;

/**
 * Created by Cais Automação on 11/11/2016. project UnicapMaps
 */
public class BuscaAEstrela implements InterfaceBuscaEmGrafo {

    private GrafoController controller;
    ArrayList<Vertice> anteriores;//came_from
    ArrayList<Integer> custos;//cost_so_far

    public BuscaAEstrela(GrafoController controller) {
        this.controller = controller;
    }

    @Override
    public ArrayList<Aresta> buscar(Vertice partida, Vertice chegada) {
        FilaOrdenada fila = new FilaOrdenada();

        ArrayList<Vertice> caminho = new ArrayList();
        anteriores = new ArrayList(); //came_from
        custos = new ArrayList();  //cost_so_far

        Vertice atual;

        for(int i = 0; i < controller.getTotalVertices(); i++){
            custos.add(Integer.MAX_VALUE);
            anteriores.add(null);
        }
        custos.set(partida.getId(), 0);

        fila.add(partida, Integer.MAX_VALUE);

        while(!(fila.isEmpty())){
            atual = fila.peek();
            for(Vertice adj: atual.getAdjacentes()){
                if(relaxarAresta(atual, adj, acharDistancia(atual, adj))){
                    int peso = custos.get(adj.getId()) + heuristica(chegada, adj);
                    fila.add(adj, peso);
                    anteriores.set(adj.getId(), atual);
                }
            }
        }
        caminho = varrerAnteriores(partida, chegada);

        return controller.getArestasFromVertices(caminho);
    }

    private boolean relaxarAresta(Vertice A, Vertice B, int custo) {
        int idA, idB, novoCusto;
        idA = A.getId();
        idB = B.getId();
        if(custos.get(idA) == Integer.MAX_VALUE){
            novoCusto = custos.get(idA);
        } else{
            novoCusto = custos.get(idA) + custo;
        }
        if(novoCusto < custos.get(idB)){
            custos.set(idB, novoCusto);
            anteriores.set(idB, A);
            return true;
        } else{
            return false;
        }
    }

    private ArrayList<Vertice> varrerAnteriores(Vertice partida,Vertice chegada){
        ArrayList<Vertice> caminho = new ArrayList();
        Vertice temp = chegada;
        while(temp != partida){
            caminho.add(temp);
            temp = anteriores.get(temp.getId());
        }
        caminho.add(partida);
        Collections.reverse(caminho);
        return caminho;
    }

    private int acharDistancia(Vertice node, Vertice alvo) {
        Aresta aresta;
        aresta = controller.getArestaFromVertices(node,alvo);
        return aresta.getCusto();
    }

    public int heuristica (Vertice a, Vertice b) {
        int x1, x2, y1, y2;

        x1 = a.getCoordenadas().getX();
        y1 = a.getCoordenadas().getY();
        x2 = b.getCoordenadas().getX();
        y2 = b.getCoordenadas().getY();

        return abs(x1 - x2) + abs(y1 - y2);
    }

    private class FilaOrdenada extends ArrayList<Vertice> {
        ArrayList<Integer> pesos;

        public FilaOrdenada() {
            super();
            pesos = new ArrayList();
        }

        public boolean add(Vertice novo, int peso) {
            Vertice verticeTemp;
            if (isEmpty()) {
                pesos.add(peso);
                return super.add(novo);
            } else {
                for (int i = 0; i < size(); i++) {
                    verticeTemp = get(i);
                    if (peso < pesos.get(i)) {
                        super.add(i, novo);
                        pesos.add(i, peso);
                        return true;
                    }
                }
                pesos.add(peso);
                return super.add(novo);

            }
        }

        public Vertice peek() {
            Vertice retorno = get(0);
            remove(0);
            pesos.remove(0);
            return retorno;
        }
    }
}