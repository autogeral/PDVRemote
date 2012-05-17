package br.com.jcomputacao.pdvremote.netlogic;

/**
 * 04/01/2010 09:57:43
 * @author Odair
 */
public enum TipoDeOperacaoEnum {

    EMITIR_CUPOM(1), LEITURA_X(2), REDUCAO_Z(3), OBTER_NUMERO_SERIE(4), GERAR_RELATORIO_SINTEGRA(5), OBTER_NUMERO_ULTIMO_CUPOM(6);
    private final int valor;

    TipoDeOperacaoEnum(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
