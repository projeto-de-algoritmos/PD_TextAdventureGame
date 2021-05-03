package game.models;

import game.Acao;
import game.models.item.Item;
import game.models.item.ItemLegivel;
import services.SugestaoEscrita;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class InterpreteJogador {

    public Acao interpretarString(String string) {

        if(string.equals("")) {
            return Acao.NADA;
        }

        List<String> comandos = Arrays.asList(string.toLowerCase().split(" "));

        String acao = comandos.get(0);

        //concatenar próximas palavras
        String complemento = " ";
        for(int i = 1; i < comandos.size(); i++){
            complemento += " " + comandos.get(i);
        }

        List<String> novosComandos = new ArrayList<>();

        novosComandos.add(acao);
        novosComandos.add(complemento.trim());

        return agir(novosComandos);
    }

    private Acao agir(List<String> comandos) throws ArrayIndexOutOfBoundsException {

        if(comandos == null || comandos.size() == 0){
            System.out.println("Faça alguma coisa !");
            return Acao.NADA;
        }

        String acaoEscolhida = comandos.get(0);
        Acao acao = null;


        String acaoProvavel = SugestaoEscrita.checkAcao(acaoEscolhida);


        out:{
            for(Acao a : Acao.values()){
                for(String alias: a.getAliases()){
                    if(acaoProvavel.compareTo(alias) == 0){
                        acao = a;
                        break out;
                    }
                }
            }
        }

        if(acao == null){
           System.out.println("Faça alguma coisa !");
           return Acao.NADA;
       }

       switch (acao.getTipoAcao()){
           case PEGAR:
               if(comandos.size() > 1){
                   String nomeItem = comandos.get(1);

                   Item item = JogoController.getJogo().coletarItem(nomeItem);

                   if(item != null) {
                       JogoController.getJogo().addItem(item);
                   }

                   JogoController.getJogo().incrementarTurnosJogador(1);

                   return acao;
               } else {
                   System.out.println("Especifique qual item deseja pegar !");
               }


               break;
           case USAR:
               if(comandos.size() > 1){
                   String nomeItem = comandos.get(1);

                   Item item = JogoController.getJogo().utilizarItemInventario(nomeItem);

                   if(item != null) {
                       item.usar();
                   }

                   JogoController.getJogo().incrementarTurnosJogador(1);

                   return acao;
               } else {
                   System.out.println("Especifique qual item deseja usar ou interagir !");
               }

               break;
           case LER:
               if(comandos.size() > 1){
                   String nomeItem = comandos.get(1);

                   Item item = JogoController.getJogo().utilizarItemInventario(nomeItem);

                   if(item != null) {
                       if(item instanceof ItemLegivel){
                           JogoController.getJogo().incrementarTurnosJogador(1);
                           item.usar();
                       } else {
                           System.out.println("Não é possível ler este item !");
                       }
                   }

                   return acao;
               } else {
                   System.out.println("Especifique qual item deseja ler !");
               }

               break;
           case ANDAR:
               if(comandos.size() > 1){
                   String nomeSalaProvavel = SugestaoEscrita.checkLocal(comandos.get(1));

                   Area salaAtual = JogoController.getJogo().getAreaAtualJogador();

                   Area areaIndicada = JogoController.getJogo().identificarAreaConectada(salaAtual, nomeSalaProvavel);
                   Integer distanciaEntreSalas = null;

                   if(areaIndicada != null) {
                       distanciaEntreSalas = JogoController.getJogo().identificaDistancia(salaAtual, areaIndicada);
                   }

                   if(areaIndicada != null && distanciaEntreSalas != null){
                       JogoController.getJogo().incrementarTurnosJogador(distanciaEntreSalas);
                       JogoController.getJogo().atualizarAreaAtual(areaIndicada);
                   } else {
                       System.out.println("Esse movimento não é permitido !");
                   }

                   return acao;
               } else {
                   System.out.println("Especifique para onde deseja ir !");
               }

               break;
           case OLHAR:
               JogoController.getJogo().identificarAreaAtual().mostrarDescricaoDetalhada();
               JogoController.getJogo().incrementarTurnosJogador(1);
               break;
           default:
               System.out.println("Esta não é uma ação válida !");
               break;
       }

       return null;

    }

}
