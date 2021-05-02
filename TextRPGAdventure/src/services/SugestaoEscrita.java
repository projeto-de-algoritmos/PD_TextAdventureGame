package services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SugestaoEscrita {
    static private final int CUSTO_GAP = 1;

    static final String[] acoes = {"olhar", "pegar", "catar", "segurar", "coletar", "andar", "mover", "locomover",
            "ir", "usar", "utilizar", "ler", "folhear",};

    static ArrayList<String> locais = new ArrayList();

    public String checkLocal(String local) {
        String localMaisProvavel = null;

        System.out.println("Você quis dizer " + localMaisProvavel + "?");

        if (!locais.isEmpty()) {
            localMaisProvavel = encontraStringMaisProvavel(local, (String[]) locais.toArray());
        }

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

    static private String encontraStringMaisProvavel(String string, String[] opcoes) {
        String acaoMaisProvavel = "";
        double menorPenalidade = 99999;

        for (int i = 0; i < opcoes.length; i++) {
            String acaoComparada = opcoes[i];
            double[][] matriz = new double[string.length()][acaoComparada.length()];
            int[][][] indexesPredecessores = new int[string.length()][acaoComparada.length()][2];

            inicializaMatriz(string, acaoComparada, matriz, indexesPredecessores);
            preencheMatriz(string, acaoComparada, matriz, indexesPredecessores);

            double penalidade = matriz[string.length() - 1][acaoComparada.length() - 1];
            if (penalidade < menorPenalidade) {
                menorPenalidade = penalidade;
                acaoMaisProvavel = acaoComparada;
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