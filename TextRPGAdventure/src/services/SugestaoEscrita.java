package services;

import game.Acao;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public final class SugestaoEscrita {
    static private final Double CUSTO_GAP = 1.0;

    private static final ArrayList<String> acoes = inicializarListaAcoes();

    private static ArrayList<String> locais = new ArrayList();

    private static final Set<Character> vowels = new HashSet<>(Arrays.asList(new Character[] { 'a', 'e', 'i', 'o', 'u' }));

    private static final Set<Character> consonants = new HashSet<>(Arrays.asList(new Character[] { 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z' }));

    private static final Set<Character> numbers = new HashSet<>(Arrays.asList(new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' }));

    private static ArrayList<String>  inicializarListaAcoes(){
        ArrayList<String> acoes = new ArrayList<>();

        for(Acao a: Acao.values()){
            for(String alias: a.getAliases()){
                acoes.add(alias);
            }
        }

        return acoes;
    }

    public static String checkLocal(String local) {
        String localMaisProvavel = null;

        if (!locais.isEmpty()) {
            localMaisProvavel = encontraStringMaisProvavel(local, locais);
        }

        System.out.println("Você quis dizer " + localMaisProvavel + "?");

        return localMaisProvavel;
    }

    static public String checkAcao(String acao) {
        String acaoMaisProvavel = encontraStringMaisProvavel(acao, acoes);

        System.out.println("Você quis dizer " + acaoMaisProvavel + "?");

        return acaoMaisProvavel;
    }

    static public void addLocal(String local) {
        locais.add(local);
    }

    static private String encontraStringMaisProvavel(String string, ArrayList<String> opcoes) {
        String acaoMaisProvavel = "";
        double menorPenalidade = 99999;

        for (String opcao: opcoes) {

            double[][] matriz = new double[string.length()][opcao.length()];
            int[][][] indexesPredecessores = new int[string.length()][opcao.length()][2];

            inicializaMatriz(string, opcao, matriz, indexesPredecessores);
            preencheMatriz(string, opcao, matriz, indexesPredecessores);

            double penalidade = matriz[string.length() - 1][opcao.length() - 1];
            if (penalidade < menorPenalidade) {
                menorPenalidade = penalidade;
                acaoMaisProvavel = opcao;
            }
        }

        return acaoMaisProvavel;
    }

    private static void preencheMatriz(String acao, String acaoComparada, double[][] matriz, int[][][] indexesPredecessores) {
        for (int j = 1; j < acaoComparada.length(); j++) {
            for (int i = 1; i < acao.length(); i++) {
                double penalidadeComAlinhamento = getPenalidade(acao.charAt(i), acaoComparada.charAt(j)) + matriz[i - 1][j - 1];
                double acaoComGap = CUSTO_GAP + matriz[i - 1][j];
                double acaoComparadaComGap = CUSTO_GAP + matriz[i][j - 1];


                double menorPenalidade = min(penalidadeComAlinhamento, acaoComGap, acaoComparadaComGap);

                if (menorPenalidade == penalidadeComAlinhamento) {
                    matriz[i][j] = penalidadeComAlinhamento;
                    indexesPredecessores[i][j][0] = i - 1;
                    indexesPredecessores[i][j][1] = j - 1;
                } else if (menorPenalidade == acaoComGap) {
                    matriz[i][j] = acaoComGap;
                    indexesPredecessores[i][j][0] = i - 1;
                    indexesPredecessores[i][j][1] = j;
                } else {
                    matriz[i][j] = acaoComparadaComGap;
                    indexesPredecessores[i][j][0] = i;
                    indexesPredecessores[i][j][1] = j - 1;
                }
            }
        }
    }

    private static double min(double num1, double num2, double num3) {
        return Double.min(Double.min(num1, num2), num3);
    }

    private static double getPenalidade(char char1, char char2) {

        if (char1 == char2) {
            return 0;
        } else {
            return DistanciaEntreTeclas.distanciaEntreTeclas(char1, char2);
        }

    }

    private static void inicializaMatriz(String acao, String acaoComparada, double[][] matriz, int[][][] indexesPredecessores) {
        for (int i = 0; i < acao.length(); i++) {
            matriz[i][0] = i * CUSTO_GAP;
            indexesPredecessores[i][0][0] = i - 1;
            indexesPredecessores[i][0][1] = 0;
        }

        for (int j = 0; j < acaoComparada.length(); j++) {
            matriz[0][j] = j * CUSTO_GAP;
            indexesPredecessores[0][j][0] = 0;
            indexesPredecessores[0][j][1] = j - 1;
        }

        indexesPredecessores[0][0][0] = -1;
        indexesPredecessores[0][0][1] = -1;
    }

}

class DistanciaEntreTeclas {
    private static final String[] TECLAS = {
            "1234567890-_=+",
            "qwertyuiop",
            "asdfghjklç",
            "zxcvbnm,.;/?"
    };

    public static double distanciaEntreTeclas(char char1, char char2) {
        double distancia = 47;

        int x1, y1;
        int x2, y2;

        x1 = y1 = x2 = y2 = -1;

        for (int i = 0; i < 4; i++) {
            List linha = TECLAS[i].chars().mapToObj(letra -> (char) letra).collect(Collectors.toList());

            if (linha.contains(char1)) {
                x1 = i + 1;
                y1 = linha.indexOf(char1) + 1;
            }

            if (linha.contains(char2)) {
                x2 = i + 1;
                y2 = linha.indexOf(char2) + 1;
            }
        }

        if (x1 > 0 && y1 > 0 && x2 > 0 && y2 > 0) {
            distancia = Math.sqrt((x2 - x1) ^ 2 + (y2 - y1) ^ 2);
        }

        return distancia;
    }
}