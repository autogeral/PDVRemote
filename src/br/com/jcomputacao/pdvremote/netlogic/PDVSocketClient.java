package br.com.jcomputacao.pdvremote.netlogic;

import br.com.jcomputacao.com.cupomRepresentacao.CupomRepresentacao;
import br.com.jcomputacao.com.cupomRepresentacao.RelatorioMFDRepresentacao;
import br.com.jcomputacao.com.cupomRepresentacao.RetornoEmissao;
import com.thoughtworks.xstream.XStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * 22/12/2009 17:45:51
 * @author Odair
 */
public class PDVSocketClient {

    private String hostname;
    private int port;
    private DataOutputStream dos = null;
    private DataInputStream dis = null;

    public PDVSocketClient(String hostName, int serverPort) {
        this.hostname = hostName;
        this.port = serverPort;
    }

    private void startUpClientConnection() {
        try {
            //cria um novo cliente, informando o host e a porta
            //a qual deve se conectar
            Socket socket = new Socket(hostname, port);

            //capta os fluxos de entrada e de saída do servidor
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar estabelecer conexão com o servidor." +
                    "\nMotivo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public RetornoEmissao enviaCupom(CupomRepresentacao cupom) throws IOException {
        startUpClientConnection();
        try {
            enviaTipoDeOperacaoASerRealizada(TipoDeOperacaoEnum.EMITIR_CUPOM);
            XStream cupomXml = new XStream();
            cupomXml.autodetectAnnotations(true);
            String str = cupomXml.toXML(cupom);
            System.out.println("Cliente STR: " + str);


            System.out.println("Enviará : " + str.length() + " caracteres");
            dos.writeUTF(str);

            //recebe o tamanho do array
            String conteudo = dis.readUTF();
            System.out.println(conteudo);
            System.out.flush();

            Object obj = cupomXml.fromXML(conteudo);
            RetornoEmissao re = null;
            if (obj instanceof RetornoEmissao) {
                re = (RetornoEmissao) obj;
            }
            return re;
        } finally {
            shutDownClientConnection();
        }
    }

    private RetornoEmissao solicitaLeituraXOuZRemota() throws IOException {
        String conteudo = dis.readUTF();
        System.out.println(conteudo);
        System.out.flush();

        XStream xStream = new XStream();
        Object obj = xStream.fromXML(conteudo);
        RetornoEmissao re = null;
        if (obj instanceof RetornoEmissao) {
            re = (RetornoEmissao) obj;
        }

        shutDownClientConnection();
        return re;
    }

    public String getEcfSerial() throws IOException {
        startUpClientConnection();
        String ecfSerial = "";
        try {
            enviaTipoDeOperacaoASerRealizada(TipoDeOperacaoEnum.OBTER_NUMERO_SERIE);
            ecfSerial = dis.readUTF();
            System.out.println("getEcfSerial em PDVSocketClient: ecfSerial: " + ecfSerial);
            System.out.flush();
        }finally{
            shutDownClientConnection();
        }
        return ecfSerial;
    }

    /**
     * Solicita o tipo de operacao que será realizada na
     * impressora fiscal remota.
     * @param tipoDeOperacao
     */
    private void enviaTipoDeOperacaoASerRealizada(TipoDeOperacaoEnum tipoDeOperacao) throws IOException {
        int valor = tipoDeOperacao.getValor();
        if(dos==null){
            throw new IllegalStateException("Chamar statup antes");
        }
        dos.writeInt(valor);
        dos.flush();
    }

    public RetornoEmissao gerarRelatorioSintegraMFD(RelatorioMFDRepresentacao relatorioMFD) throws IOException {
        startUpClientConnection();
        try {
            enviaTipoDeOperacaoASerRealizada(TipoDeOperacaoEnum.GERAR_RELATORIO_SINTEGRA);
            XStream mfdXML = new XStream();
            String str = mfdXML.toXML(relatorioMFD);

            dos.writeUTF(str);
            String conteudo = dis.readUTF();
            System.out.println(conteudo);
            System.out.flush();

            Object obj = mfdXML.fromXML(conteudo);
            RetornoEmissao re = null;
            if (obj instanceof RetornoEmissao) {
                re = (RetornoEmissao) obj;
            }


            //recebe o arquivo remoto e salva localmente
            File file = new File(relatorioMFD.getPathDeDestino());
            DataOutputStream relatorioDos = new DataOutputStream(new FileOutputStream(file));
            try {
                relatorioDos.writeUTF(str);
                relatorioDos.close();
            } catch (ArrayIndexOutOfBoundsException a) {
                Logger.getLogger(getClass().getName()).log(Level.ALL, "Cliente: Erro ao receber arquivo remoto" + a.getMessage());
            }
            return re;
        } finally {
            shutDownClientConnection();
        }
    }

    public boolean leituraX() throws IOException {
        boolean result = false;
        startUpClientConnection();
        try {
            enviaTipoDeOperacaoASerRealizada(TipoDeOperacaoEnum.LEITURA_X);
            result = true;
        }finally{
            shutDownClientConnection();
        }
        return result;
    }

    public boolean reducaoZ() throws IOException {
        boolean result = false;
        startUpClientConnection();
        try {
            enviaTipoDeOperacaoASerRealizada(TipoDeOperacaoEnum.REDUCAO_Z);
            result = true;
        }finally{
            shutDownClientConnection();
        }
        return result;
    }

    private void shutDownClientConnection() throws IOException {
        if (dos != null) {
            dos.close();
        }

        if (dis != null) {
            dis.close();
        }
    }

    public int getUltimoCupom() throws IOException {
        startUpClientConnection();
        int ultimoCupom = 0;
        try {
            enviaTipoDeOperacaoASerRealizada(TipoDeOperacaoEnum.OBTER_NUMERO_ULTIMO_CUPOM);
            ultimoCupom = dis.readInt();
            System.out.println("getUltimoCupom em PDVSocketClient: ecfSerial: " + ultimoCupom);
            System.out.flush();
        }finally{
            shutDownClientConnection();
        }
        return ultimoCupom;
    }
}
