package pneus.vachileski_mobi_funcoes_genericas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@SuppressWarnings("SpellCheckingInspection")
public class EnviaEmail extends javax.mail.Authenticator {
    private final String filial;
    private String usuario = "";
    private String senha = "";
    private String porta = "587";
    private String portaSegura = "";
    private String servidor = "";
    private String remetente;
    private String assunto;
    private String corpoEmail;

    public String[] para;
    private String[] comCopia;

    private boolean isAutentica = false;
    private boolean isUsaSSL = false;
    private final boolean isHtml;
    private final boolean isDebug;

    private final MimeMultipart _multipart;

    ConexaoBDExt conecta = new ConexaoBDExt();

    public EnviaEmail(String tipoEmail, String filial) {
        /* E-MAIL OST **/
        if (filial.contains("1101") || (filial.contains("1201"))) {
            filial = "110101";
        } else if (filial.contains("1301") || (filial.contains("1401"))) {
            filial = "140101"; //LS PNEUS ESTÁ CONFIGURADO O E-MAIL DA @lauxen
        } else {
            filial = "020101";
        }

        try {
            ResultSet resultSet = conecta.buscaConfigEmail(filial);
            resultSet.last();
            if (resultSet.getRow() > 0) {
                resultSet.beforeFirst();
                while (resultSet.next()) {
                    servidor = resultSet.getString("SERVIDOR").split(":")[0];
                    if (servidor.contains("gmail") || (servidor.contains("lauxen"))) {
                        if (resultSet.getString("SSL").equals("S")) {
                            porta = "465";
                        }
                    } else {
                        porta = resultSet.getString("SERVIDOR").split(":")[1];
                    }
                    portaSegura = porta;
                    remetente = usuario = resultSet.getString("CONTA");
                    senha = resultSet.getString("SENHA");
                    isAutentica = resultSet.getString("AUTENTICA").equals("S");
                    isUsaSSL = resultSet.getString("SSL").equals("S");

                    GettersSetters.setIsCfgEmailOk(true);
                }
            } else {
                GettersSetters.setIsCfgEmailOk(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            GettersSetters.setErroEnvioColetaBDExt("" + e);
        }

//        /* E-MAIL OST **/
//        if (filial.contains("1101") || (filial.contains("1201"))) {
//            _host = "smtp.office365.com"; //"mail.vachileski.com.br";
//            _port = "25"; // porta de envio, geralmente 587, 465 ou em último caso a 25 -> ALTERADO PARA 25 EM 09/03 POIS ESTAVA DANDO ERRO DE ENVIO NO DOMINO @OST
//            _sport = "25"; // ALTERADO PARA 25 EM 09/03 POIS ESTAVA DANDO ERRO DE ENVIO NO DOMINO @OST
//
//            _from = _user = "nfe@ost.ind.br"; // usuário e email do destinatário
//            _pass = "N0t4$f1$C"; // senha
//
//            _auth = true;
//            _useSsl = true; //gmail usa true
//
//            /* E-MAIL LAUXEN **/
//        } else if (filial.contains("1301") || (filial.contains("1401"))) {
//            _host = "mail.lauxenpneus.com.br"; //"mail.vachileski.com.br";
//            _port = "587"; // porta de envio, geralmente 587, 465 ou em último caso a 25
//            _sport = "587"; // porta socket padrão, mesmas instruções anteriores
//
//            _from = _user = "faturamento@lauxenpneus.com.br"; // usuário e email do destinatário
//            _pass = "lauxenf1530"; // senha
//
//            _auth = true;
//            _useSsl = false; //gmail usa true
//        } else {
//            _host = "smtp.gmail.com"; //"mail.vachileski.com.br";
//            _port = "465"; // porta de envio, geralmente 587, 465 ou em último caso a 25
//            _sport = "465"; // porta socket padrão, mesmas instruções anteriores
//
//            _from = _user = "nfe@vachileski.com.br"; // usuário e email do destinatário
//            _pass = "nfe2019*190"; // senha
//
//            _auth = true;
//            _useSsl = true; //gmail usa true
//        }

        isDebug = false;
        assunto = ""; // assunto
        corpoEmail = ""; // mensagem html

        isHtml = tipoEmail.equals("COLETA"); //diz se a mensagem é HTML ou texto puro
        this.filial = filial;
        _multipart = new MimeMultipart();
    }

    public boolean send() throws MessagingException {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        boolean enviado = false;
        if (!usuario.equals("") && !senha.equals("") && getPara().length > 0 && !remetente.equals("") && !assunto.equals("")) {
            Session session = Session.getInstance(_setProperties(), new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(remetente, senha);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(getFrom()));

            Address[] addressTo = new InternetAddress[para.length];
            for (int i = 0; i < para.length; i++) {
                addressTo[i] = new InternetAddress(para[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

            //se tem de enviar cópia oculta para alguém
            if (comCopia != null && comCopia.length > 0) {
                Address[] addressCco = new InternetAddress[comCopia.length];
                for (int i = 0; i < comCopia.length; i++) {
                    addressCco[i] = new InternetAddress(comCopia[i]);
                }
                msg.addRecipients(Message.RecipientType.BCC, addressCco);
            }

            msg.setSubject(assunto);
            msg.setSentDate(new Date());

            // corpo da mensagem
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(corpoEmail);
            if (isHtml) {
                messageBodyPart.setHeader("lang", "pt-br");
                messageBodyPart.setHeader("charset", "UTF-8");
                messageBodyPart.setHeader("content-type", "text/html");
            }
            _multipart.addBodyPart(messageBodyPart);
            msg.setContent(_multipart);

            // envia o email
            try {
                Transport.send(msg);
                enviado = true;
            } catch (MessagingException e) {
                GettersSetters.setErroEnvioColetaBDExt("" + e);
                e.printStackTrace();
            }
        }
        return enviado;
    }

    public void addAttachment(String filename, String nomeCli) throws Exception {
        filename = filename.replace("file:", "").replace("//", "/");
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        if (nomeCli == null) {
            messageBodyPart.setFileName(filename);
        } else {
            messageBodyPart.setFileName("Coleta Eletronica de " + nomeCli + ".pdf");
        }
        _multipart.addBodyPart(messageBodyPart);
    }

    private Properties _setProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", servidor);
        props.put("mail.smtp.port", porta);

        if (isDebug) props.put("mail.debug", "true");
        if (isAutentica) props.put("mail.smtp.auth", "true");

        if (isUsaSSL) {
            if (filial.contains("1101") || filial.contains("1201")) {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2"); //ADICIONADO EM 09/03
            } else {
                props.put("mail.smtp.socketFactory.port", portaSegura);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
            }
        }
        return props;
    }


    public void setBody(String _body) {
        this.corpoEmail = _body;
    }

    public void setTo(String[] toArr) {
        this.para = toArr;
    }

    public String[] getPara() {
        return para;
    }

    public void setFrom(String string) {
        this.remetente = string;
    }

    public String getFrom() {
        return remetente;
    }

    public void setSubject(String string) {
        this.assunto = string;
    }
}