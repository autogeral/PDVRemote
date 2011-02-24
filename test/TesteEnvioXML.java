
import br.com.jcomputacao.com.cupomRepresentacao.AcrescimoDesconto;
import br.com.jcomputacao.com.cupomRepresentacao.CupomRepresentacao;
import br.com.jcomputacao.com.cupomRepresentacao.ItemRepresentacao;
import br.com.jcomputacao.com.cupomRepresentacao.ModoPagamentoRepresentacao;
import br.com.jcomputacao.com.cupomRepresentacao.TipoAcrescimoDesconto;
import br.com.jcomputacao.com.cupomRepresentacao.TipoQuantidade;
import br.com.jcomputacao.pdvremote.netlogic.PDVSocketClient;
import java.io.IOException;

/**
 * 23/12/2009 16:28:40
 * @author Odair
 */
public class TesteEnvioXML {

    public static void main(String[] args) throws IOException {

        CupomRepresentacao cupom = new CupomRepresentacao();
        cupom.setAcrescimoDescontoNoCupom(AcrescimoDesconto.DESCONTO);
        cupom.setCpfCnpj("111.111.111-11");
        cupom.setTipoAcrescimoDescontoNaVenda(TipoAcrescimoDesconto.VALOR);
        cupom.setValorAcrescimoDescontoNaVenda(100.00d);
        cupom.setValorTotalDaVenda(200.00d);

        ItemRepresentacao item = new ItemRepresentacao();
        item.setAcrescimoDesconto(AcrescimoDesconto.DESCONTO);
        item.setAliquota(0.18d);
        item.setDepartamento("DEPTO");
        item.setDescricao("Ferrari F50");
        item.setIsUtilizaDepartamento(true);
        item.setIsUtilizaUnidadeMedida(true);
        item.setItemCodigo("YYYBB");
        item.setQuantidade(2f);
        item.setTipoAcrescimoDesconto(TipoAcrescimoDesconto.VALOR);
        item.setTipoDeQuantidade(TipoQuantidade.INTEIRA);
        item.setUnidadeMedida("UN");
        item.setValor(200.00d);
        item.setValorDoAcrescimoDesconto(0.00d);
        item.setValorUnitario(200.00d);

        cupom.addItem(item);

        ModoPagamentoRepresentacao modoPagamento = new ModoPagamentoRepresentacao();
        modoPagamento.setDescricaoFormaDePagamento("DINHEIRO");
        modoPagamento.setValor(200.00d);

        cupom.addModoDePagamento(modoPagamento);
        PDVSocketClient clientSocket = new PDVSocketClient("localhost", 4444);
        clientSocket.enviaCupom(cupom);
    }
}
