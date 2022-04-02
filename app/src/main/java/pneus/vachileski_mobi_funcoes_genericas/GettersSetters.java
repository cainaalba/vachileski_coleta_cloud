package pneus.vachileski_mobi_funcoes_genericas;

import android.annotation.SuppressLint;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings("ALL")
public class GettersSetters {
    public static int VERSAO = ConexaoBDInt.VERSAO; // versão do BD SQLite

    public static long tempoPing = 0;
    public static String dataColetaBR = "";
    public static String dataColetaEN = "";
    public static String horaLanc = "";
    public static String usuarioLogado = "";
    public static String idUsuarioLogado = "";
    public static String codigoVendedor = "";
    public static String filial = "";
    public static String descrFilial = "";
    public static String nomeVend = "";
    public static String stringConexao = "Offline";
    public static String clienteBlq = "";
    public static String conexaoBD = "0";
    public static Boolean validaConexaoBD = false;
    public static Boolean validaConexao = true;
    public static String tipoUsuario = "";
    public static boolean atualizarClientes = false;
    public static boolean atualizarProdutos = false;
    public static ArrayList<String> arrClientesPermitidos = new ArrayList<String>();
    public static boolean isSessionFoto = false;
    public static boolean isSessionAssinat = false;
    public static boolean isLogin = false;
    public static boolean isCfgEmailOk = false;
    public static String numeroColeta = "";

    // Daods cliente
    public static String docCli = "";
    public static String codCli = "";
    public static String lojaCli = "";
    public static String rzSocialCli = "";
    public static String inscrCli = "";
    public static String emailCli = "";
    public static String endEntregaCli = "";
    public static String bairroEntregaCli = "";
    public static String municEntregaCli = "";
    public static String estEntregaCli = "";
    public static String cepEntregaCli = "";
    public static String dddEntregaCli = "";
    public static String foneEntregaCli = "";
    public static String endCobranCli = "";
    public static String bairroCobranCli = "";
    public static String municCobranCli = "";
    public static String estCobranCli = "";
    public static String cepCobranCli = "";
    public static String dddCobranCli = "";
    public static String foneCobranCli = "";
    public static boolean validarDadosCli = true;
    public static String tipoVendedor = "";
    public static double descExclusivo = 0;
    public static boolean clienteGrp = false;
    public static double descGrupo = 0;
    public static String categoriaCli = "";
    public static String tabelaPrcCli = "";

    // Coleta
    public static String valorColeta = "";
    public static String qtdItensColeta = "";

    // Variáveis condições de pagamento
    public static String codCondPgto = "";
    public static String condPgto = "";
    public static String descrCondPgto = "";
    public static String descrFormaPgto = "";

    // Variávels Borracheiros
    public static String tipoColeta = "";
    public static String descrTipoColeta = "";
    public static boolean checkComissBorr = false;
    public static boolean isOrcamento = false;
    public static String pathAssinBorr = null;
    public static String picNameAssinBorr = "";
    public static String nomeBorracheiro = "";
    public static String codBorracheiro = "";
    public static String docBorracheiro = "";
    public static String infoAdicionais = "";
    public static boolean isBorracheiro = false;
    public static boolean isBorracheiroCli = false; //ALTERAÇÃO CMD 959
    public static boolean isBorracheiroVendPat = false; //ALTERAÇÃO CMD 959
    public static String codBorrachPatio = "";

    // Variável aceite termos
    public static boolean aceiteTermos = false;
    public static String tipoAssinatura = "";

    // Assinatura Cliente
    public static String pathAssinCli = null;
    public static String picNameAssinCli = "";
    public static String referenciasComerciais = "";

    public static String imagePath = "";

    public static ArrayList<String> arrItemColeta = new ArrayList<>();
    public static ArrayList<String> arrQtdItemColeta = new ArrayList<>();
    public static ArrayList<String> arrCodProdColeta = new ArrayList<>();
    public static ArrayList<String> arrBitolaColeta = new ArrayList<>();
    public static ArrayList<String> arrMarcaColeta = new ArrayList<>();
    public static ArrayList<String> arrModeloColeta = new ArrayList<>();
    public static ArrayList<String> arrSerieColeta = new ArrayList<>();
    public static ArrayList<String> arrDotColeta = new ArrayList<>();
    public static ArrayList<String> arrMontadoColeta = new ArrayList<>();
    public static ArrayList<String> arrDesenhoColeta = new ArrayList<>();
    public static ArrayList<String> arrValorUnit = new ArrayList<>();
    public static ArrayList<String> arrValorTotal = new ArrayList<>();
    public static ArrayList<String> arrUrgenteColeta = new ArrayList<>();
    public static ArrayList<String> arrPorcComisBorr = new ArrayList<>();
    public static ArrayList<String> arrValrComisBorr = new ArrayList<>();
    public static ArrayList<String> arrObsItens = new ArrayList<>();
    public static ArrayList<String> arrCAgua = new ArrayList<>(); //0=Não, 1=Sim
    public static ArrayList<String> arrCCamara = new ArrayList<>(); //0=Não, 1=Sim
    public static ArrayList<String> arrGarantia = new ArrayList<>(); //0=Não, 1=Sim

    //Arrays usados na importação de itens quando coletas de refaturamento
    public static ArrayList<String> arrFilialOri = new ArrayList<>();
    public static ArrayList<String> arrColetaOri = new ArrayList<>();
    public static ArrayList<String> arrItemOri = new ArrayList<>();

    public static ArrayList<String> arrVlrBlqIt = new ArrayList<>();
    public static ArrayList<String> arrBaseCalcIt = new ArrayList<>();
    
    public static String tipoFoto = "";
    public static String tipoDoc = "";
    public static boolean docsNovoCliOK = true;
    public static String pathDocsNovoCli = "";
    public static String picNameRGCli = "";
    public static String picNameDocCli = "";
    public static String picNameComprResidCli = "";
    public static File fotoCapturada = null;

    public static boolean enviarColeta = false;
    public static boolean execThreadCheckConn = true;
    public static boolean execThreadCheckUser = true;
    public static boolean envioColetaBDExt = false;
    public static String erroEnvioColetaBDExt = "";
    public static String erroSalvarColetaBDInt = "";
    public static String emailUsuario = "";
    public static String opcSqlite = "";
    public static String codTotvs = "";
    public static String nomeTotvs = "";
    public static boolean isNumeroInserido = false;
    public static String seqIdenficador = "";
    public static String expSqLite = "";

    public static boolean isIsCfgEmailOk() {
        return isCfgEmailOk;
    }

    public static void setIsCfgEmailOk(boolean isCfgEmailOk) {
        GettersSetters.isCfgEmailOk = isCfgEmailOk;
    }

    public static String getCodBorrachPatio() {
        return codBorrachPatio;
    }

    public static void setCodBorrachPatio(String codBorrachPatio) {
        GettersSetters.codBorrachPatio = codBorrachPatio;
    }

    public static boolean isIsBorracheiroVendPat() {
        return isBorracheiroVendPat;
    }

    public static void setIsBorracheiroVendPat(boolean isBorracheiroVendPat) {
        GettersSetters.isBorracheiroVendPat = isBorracheiroVendPat;
    }

    public static boolean isIsLogin() {
        return isLogin;
    }

    public static void setIsLogin(boolean isLogin) {
        GettersSetters.isLogin = isLogin;
    }

    public static boolean isIsBorracheiroCli() {
        return isBorracheiroCli;
    }

    public static void setIsBorracheiroCli(boolean isBorracheiroCli) {
        GettersSetters.isBorracheiroCli = isBorracheiroCli;
    }

    public static boolean isIsSessionFoto() {
        return isSessionFoto;
    }

    public static void setIsSessionFoto(boolean isSessionFoto) {
        GettersSetters.isSessionFoto = isSessionFoto;
    }

    public static boolean isIsSessionAssinat() {
        return isSessionAssinat;
    }

    public static void setIsSessionAssinat(boolean isSessionAssinat) {
        GettersSetters.isSessionAssinat = isSessionAssinat;
    }

    public static String getExpSqLite() {
        return expSqLite;
    }

    public static void setExpSqLite(String expSqLite) {
        GettersSetters.expSqLite = expSqLite;
    }

    public static ArrayList<String> getArrClientesPermitidos() {
        return arrClientesPermitidos;
    }

    public static void setArrClientesPermitidos(ArrayList<String> arrClientesPermitidos) {
        GettersSetters.arrClientesPermitidos = arrClientesPermitidos;
    }

    public static long getTempoPing() {
        return tempoPing;
    }

    public static void setTempoPing(long tempoPing) {
        GettersSetters.tempoPing = tempoPing;
    }

    public static Boolean getValidaConexao() {
        return validaConexao;
    }

    public static void setValidaConexao(Boolean validaConexao) {
        GettersSetters.validaConexao = validaConexao;
    }

    public static String getImagePath() {
        return imagePath;
    }

    public static void setImagePath(String imagePath) {
        GettersSetters.imagePath = imagePath;
    }

    public static String getSeqIdenficador() {
        return seqIdenficador;
    }

    public static void setSeqIdenficador(String seqIdenficador) {
        GettersSetters.seqIdenficador = seqIdenficador;
    }

    public static boolean isAtualizarClientes() {
        return atualizarClientes;
    }

    public static void setAtualizarClientes(boolean atualizarClientes) {
        GettersSetters.atualizarClientes = atualizarClientes;
    }

    public static boolean isAtualizarProdutos() {
        return atualizarProdutos;
    }

    public static void setAtualizarProdutos(boolean atualizarProdutos) {
        GettersSetters.atualizarProdutos = atualizarProdutos;
    }

    public static String getTabelaPrcCli() {
        return tabelaPrcCli;
    }

    public static void setTabelaPrcCli(String tabelaPrcCli) {
        GettersSetters.tabelaPrcCli = tabelaPrcCli;
    }

    public static boolean isIsNumeroInserido() {
        return isNumeroInserido;
    }

    public static void setIsNumeroInserido(boolean isNumeroInserido) {
        GettersSetters.isNumeroInserido = isNumeroInserido;
    }

    public static String getOpcSqlite() {
        return opcSqlite;
    }

    public static void setOpcSqlite(String opcSqlite) {
        GettersSetters.opcSqlite = opcSqlite;
    }

    public static String getCodTotvs() {
        return codTotvs;
    }

    public static void setCodTotvs(String codTotvs) {
        GettersSetters.codTotvs = codTotvs;
    }

    public static String getNomeTotvs() {
        return nomeTotvs;
    }

    public static void setNomeTotvs(String nomeTotvs) {
        GettersSetters.nomeTotvs = nomeTotvs;
    }

    public static int getVERSAO() {
        return VERSAO;
    }

    public static void setVERSAO(int VERSAO) {
        GettersSetters.VERSAO = VERSAO;
    }

    public static String getEmailUsuario() {
        return emailUsuario;
    }

    public static void setEmailUsuario(String emailUsuario) {
        GettersSetters.emailUsuario = emailUsuario;
    }

    public static String getErroEnvioColetaBDExt() {
        return erroEnvioColetaBDExt;
    }

    public static void setErroEnvioColetaBDExt(String erroEnvioColetaBDExt) {
        GettersSetters.erroEnvioColetaBDExt = erroEnvioColetaBDExt;
    }

    public static String getErroSalvarColetaBDInt() {
        return erroSalvarColetaBDInt;
    }

    public static void setErroSalvarColetaBDInt(String erroSalvarColetaBDInt) {
        GettersSetters.erroSalvarColetaBDInt = erroSalvarColetaBDInt;
    }

    public static String getTipoUsuario() {
        return tipoUsuario;
    }

    public static void setTipoUsuario(String tipoUsuario) {
        GettersSetters.tipoUsuario = tipoUsuario;
    }

    public static boolean isExecThreadCheckUser() {
        return execThreadCheckUser;
    }

    public static void setExecThreadCheckUser(boolean execThreadCheckUser) {
        GettersSetters.execThreadCheckUser = execThreadCheckUser;
    }

    public static boolean isEnviarColeta() {
        return enviarColeta;
    }

    public static void setEnviarColeta(boolean enviarColeta) {
        GettersSetters.enviarColeta = enviarColeta;
    }

    public static String getReferenciasComerciais() {
        return referenciasComerciais;
    }

    public static void setReferenciasComerciais(String referenciasComerciais) {
        GettersSetters.referenciasComerciais = referenciasComerciais;
    }

    public static String getCategoriaCli() {
        return categoriaCli;
    }

    public static void setCategoriaCli(String categoriaCli) {
        GettersSetters.categoriaCli = categoriaCli;
    }

    public static boolean isValidarDadosCli() {
        return validarDadosCli;
    }

    public static void setValidarDadosCli(boolean validarDadosCli) {
        GettersSetters.validarDadosCli = validarDadosCli;
    }

    public static double getDescExclusivo() {
        return descExclusivo;
    }

    public static void setDescExclusivo(double descExclusivo) {
        GettersSetters.descExclusivo = descExclusivo;
    }

    public static boolean isClienteGrp() {
        return clienteGrp;
    }

    public static void setClienteGrp(boolean clienteGrp) {
        GettersSetters.clienteGrp = clienteGrp;
    }

    public static double getDescGrupo() {
        return descGrupo;
    }

    public static void setDescGrupo(double descGrupo) {
        GettersSetters.descGrupo = descGrupo;
    }

    public static String getTipoColeta() {
        return tipoColeta;
    }

    public static void setTipoColeta(String tipoColeta) {
        GettersSetters.tipoColeta = tipoColeta;
    }

    public static String getDescrTipoColeta() {
        return descrTipoColeta;
    }

    public static void setDescrTipoColeta(String descrTipoColeta) {
        GettersSetters.descrTipoColeta = descrTipoColeta;
    }

    public static String getDataColetaEN() {
        return dataColetaEN;
    }

    public static void setDataColetaEN(String dataColetaEN) {
        GettersSetters.dataColetaEN = dataColetaEN;
    }

    public static File getFotoCapturada() {
        return fotoCapturada;
    }

    public static void setFotoCapturada(File fotoCapturada) {
        GettersSetters.fotoCapturada = fotoCapturada;
    }

    public static String getTipoFoto() {
        return tipoFoto;
    }

    public static void setTipoFoto(String tipoFoto) {
        GettersSetters.tipoFoto = tipoFoto;
    }

    public static String getTipoDoc() {
        return tipoDoc;
    }

    public static void setTipoDoc(String tipoDoc) {
        GettersSetters.tipoDoc = tipoDoc;
    }

    public static String getPathDocsNovoCli() {
        return pathDocsNovoCli;
    }

    public static void setPathDocsNovoCli(String pathDocsNovoCli) {
        GettersSetters.pathDocsNovoCli = pathDocsNovoCli;
    }

    public static String getPicNameRGCli() {
        return picNameRGCli;
    }

    public static void setPicNameRGCli(String picNameRGCli) {
        GettersSetters.picNameRGCli = picNameRGCli;
    }

    public static String getPicNameDocCli() {
        return picNameDocCli;
    }

    public static void setPicNameDocCli(String picNameDocCli) {
        GettersSetters.picNameDocCli = picNameDocCli;
    }

    public static String getPicNameComprResidCli() {
        return picNameComprResidCli;
    }

    public static void setPicNameComprResidCli(String picNameComprResidCli) {
        GettersSetters.picNameComprResidCli = picNameComprResidCli;
    }

    public static boolean isDocsNovoCliOK() {
        return docsNovoCliOK;
    }

    public static void setDocsNovoCliOK(boolean docsNovoCliOK) {
        GettersSetters.docsNovoCliOK = docsNovoCliOK;
    }

    public static ArrayList<String> getArrPorcComisBorr() {
        return arrPorcComisBorr;
    }

    public static void setArrPorcComisBorr(ArrayList<String> arrPorcComisBorr) {
        GettersSetters.arrPorcComisBorr = arrPorcComisBorr;
    }

    public static ArrayList<String> getArrValrComisBorr() {
        return arrValrComisBorr;
    }

    public static void setArrValrComisBorr(ArrayList<String> arrValrComisBorr) {
        GettersSetters.arrValrComisBorr = arrValrComisBorr;
    }

    public static ArrayList<String> getArrObsItens() {
        return arrObsItens;
    }

    public static void setArrObsItens(ArrayList<String> arrObsItens) {
        GettersSetters.arrObsItens = arrObsItens;
    }

    public static ArrayList<String> getArrCAgua() {
        return arrCAgua;
    }

    public static void setArrCAgua(ArrayList<String> arrCAgua) {
        GettersSetters.arrCAgua = arrCAgua;
    }

    public static ArrayList<String> getArrCCamara() {
        return arrCCamara;
    }

    public static void setArrCCamara(ArrayList<String> arrCCamara) {
        GettersSetters.arrCCamara = arrCCamara;
    }

    public static ArrayList<String> getArrGarantia() {
        return arrGarantia;
    }

    public static void setArrGarantia(ArrayList<String> arrGarantia) {
        GettersSetters.arrGarantia = arrGarantia;
    }

    public static ArrayList<String> getArrFilialOri() {
        return arrFilialOri;
    }

    public static void setArrFilialOri(ArrayList<String> arrFilialOri) {
        GettersSetters.arrFilialOri = arrFilialOri;
    }

    public static ArrayList<String> getArrColetaOri() {
        return arrColetaOri;
    }

    public static void setArrColetaOri(ArrayList<String> arrColetaOri) {
        GettersSetters.arrColetaOri = arrColetaOri;
    }

    public static ArrayList<String> getArrItemOri() {
        return arrItemOri;
    }

    public static void setArrItemOri(ArrayList<String> arrItemOri) {
        GettersSetters.arrItemOri = arrItemOri;
    }

    public static ArrayList<String> getArrVlrBlqIt() {
        return arrVlrBlqIt;
    }

    public static void setArrVlrBlqIt(ArrayList<String> arrVlrBlqIt) {
        GettersSetters.arrVlrBlqIt = arrVlrBlqIt;
    }

    public static ArrayList<String> getArrBaseCalcIt() {
        return arrBaseCalcIt;
    }

    public static void setArrBaseCalcIt(ArrayList<String> arrBaseCalcIt) {
        GettersSetters.arrBaseCalcIt = arrBaseCalcIt;
    }

    public static String getDocBorracheiro() {
        return docBorracheiro;
    }

    public static void setDocBorracheiro(String docBorracheiro) {
        GettersSetters.docBorracheiro = docBorracheiro;
    }

    public static String getCodBorracheiro() {
        return codBorracheiro;
    }

    public static void setCodBorracheiro(String codBorracheiro) {
        GettersSetters.codBorracheiro = codBorracheiro;
    }

    public static String getNomeBorracheiro() {
        return nomeBorracheiro;
    }

    public static void setNomeBorracheiro(String nomeBorracheiro) {
        GettersSetters.nomeBorracheiro = nomeBorracheiro;
    }

    public static boolean isIsBorracheiro() {
        return isBorracheiro;
    }

    public static void setIsBorracheiro(boolean isBorracheiro) {
        GettersSetters.isBorracheiro = isBorracheiro;
    }

    public static String getTipoAssinatura() {
        return tipoAssinatura;
    }

    public static void setTipoAssinatura(String tipoAssinatura) {
        GettersSetters.tipoAssinatura = tipoAssinatura;
    }

    public static String getPicNameAssinCli() {
        return picNameAssinCli;
    }

    public static void setPicNameAssinCli(String picNameAssinCli) {
        GettersSetters.picNameAssinCli = picNameAssinCli;
    }

    public static String getPicNameAssinBorr() {
        return picNameAssinBorr;
    }

    public static void setPicNameAssinBorr(String picNameAssinBorr) {
        GettersSetters.picNameAssinBorr = picNameAssinBorr;
    }

    public static String getPathAssinBorr() {
        return pathAssinBorr;
    }

    public static void setPathAssinBorr(String pathAssinBorr) {
        GettersSetters.pathAssinBorr = pathAssinBorr;
    }

    public static String getPathAssinCli() {
        return pathAssinCli;
    }

    public static void setPathAssinCli(String pathAssinCli) {
        GettersSetters.pathAssinCli = pathAssinCli;
    }

    public static boolean isAceiteTermos() {
        return aceiteTermos;
    }

    public static void setAceiteTermos(boolean aceiteTermos) {
        GettersSetters.aceiteTermos = aceiteTermos;
    }

    public static boolean isCheckComissBorr() {
        return checkComissBorr;
    }

    public static void setCheckComissBorr(boolean checkComissBorr) {
        GettersSetters.checkComissBorr = checkComissBorr;
    }

    public static String getInfoAdicionais() {
        return infoAdicionais;
    }

    public static void setInfoAdicionais(String infoAdicionais) {
        GettersSetters.infoAdicionais = infoAdicionais;
    }

    public static String getDescrFormaPgto() {
        return descrFormaPgto;
    }

    public static void setDescrFormaPgto(String descrFormaPgto) {
        GettersSetters.descrFormaPgto = descrFormaPgto;
    }

    public static String getCodCondPgto() {
        return codCondPgto;
    }

    public static void setCodCondPgto(String codCondPgto) {
        GettersSetters.codCondPgto = codCondPgto;
    }

    public static String getDescrCondPgto() {
        return descrCondPgto;
    }

    public static void setDescrCondPgto(String descrCondPgto) {
        GettersSetters.descrCondPgto = descrCondPgto;
    }

    public static String getCondPgto() {
        return condPgto;
    }

    public static void setCondPgto(String condPgto) {
        GettersSetters.condPgto = condPgto;
    }

    public static ArrayList<String> getArrItemColeta() {
        return arrItemColeta;
    }

    public static void setArrItemColeta(ArrayList<String> arrItemColeta) {
        GettersSetters.arrItemColeta = arrItemColeta;
    }

    public static ArrayList<String> getArrQtdItemColeta() {
        return arrQtdItemColeta;
    }

    public static void setArrQtdItemColeta(ArrayList<String> arrQtdColeta) {
        GettersSetters.arrQtdItemColeta = arrQtdColeta;
    }

    public static ArrayList<String> getArrBitolaColeta() {
        return arrBitolaColeta;
    }

    public static void setArrBitolaColeta(ArrayList<String> arrBitolaColeta) {
        GettersSetters.arrBitolaColeta = arrBitolaColeta;
    }

    public static ArrayList<String> getArrCodProdColeta() {
        return arrCodProdColeta;
    }

    public static void setArrCodProdColeta(ArrayList<String> arrCodProdColeta) {
        GettersSetters.arrCodProdColeta = arrCodProdColeta;
    }

    public static ArrayList<String> getArrMarcaColeta() {
        return arrMarcaColeta;
    }

    public static void setArrMarcaColeta(ArrayList<String> arrMarcaColeta) {
        GettersSetters.arrMarcaColeta = arrMarcaColeta;
    }

    public static ArrayList<String> getArrModeloColeta() {
        return arrModeloColeta;
    }

    public static void setArrModeloColeta(ArrayList<String> arrModeloColeta) {
        GettersSetters.arrModeloColeta = arrModeloColeta;
    }

    public static ArrayList<String> getArrSerieColeta() {
        return arrSerieColeta;
    }

    public static void setArrSerieColeta(ArrayList<String> arrSerieColeta) {
        GettersSetters.arrSerieColeta = arrSerieColeta;
    }

    public static ArrayList<String> getArrDotColeta() {
        return arrDotColeta;
    }

    public static void setArrDotColeta(ArrayList<String> arrDotColeta) {
        GettersSetters.arrDotColeta = arrDotColeta;
    }

    public static ArrayList<String> getArrMontadoColeta() {
        return arrMontadoColeta;
    }

    public static void setArrMontadoColeta(ArrayList<String> arrMontadoColeta) {
        GettersSetters.arrMontadoColeta = arrMontadoColeta;
    }

    public static ArrayList<String> getArrDesenhoColeta() {
        return arrDesenhoColeta;
    }

    public static void setArrDesenhoColeta(ArrayList<String> arrDesenhoColeta) {
        GettersSetters.arrDesenhoColeta = arrDesenhoColeta;
    }

    public static ArrayList<String> getArrValorUnit() {
        return arrValorUnit;
    }

    public static void setArrValorUnit(ArrayList<String> arrValorUnit) {
        GettersSetters.arrValorUnit = arrValorUnit;
    }

    public static ArrayList<String> getArrValorTotal() {
        return arrValorTotal;
    }

    public static void setArrValorTotal(ArrayList<String> arrValorTotal) {
        GettersSetters.arrValorTotal = arrValorTotal;
    }

    public static ArrayList<String> getArrUrgenteColeta() {
        return arrUrgenteColeta;
    }

    public static void setArrUrgenteColeta(ArrayList<String> arrUrgenteColeta) {
        GettersSetters.arrUrgenteColeta = arrUrgenteColeta;
    }

    public static String getValorColeta() {
        return valorColeta;
    }

    public static void setValorColeta(String valorColeta) {
        GettersSetters.valorColeta = valorColeta;
    }

    public static String getQtdItensColeta() {
        return qtdItensColeta;
    }

    public static void setQtdItensColeta(String qtdItensColeta) {
        GettersSetters.qtdItensColeta = qtdItensColeta;
    }

    public static String getDataColetaBR() {
        return dataColetaBR;
    }

    public static void setDataColetaBR(String dataColetaBR) {
        GettersSetters.dataColetaBR = dataColetaBR;
    }

    public static String getHoraLanc() {
        return horaLanc;
    }

    public static void setHoraLanc(String horaLanc) {
        GettersSetters.horaLanc = horaLanc;
    }

    public static String getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void setUsuarioLogado(String usuarioLogado) {
        GettersSetters.usuarioLogado = usuarioLogado;
    }

    public static String getIdUsuarioLogado() {
        return idUsuarioLogado;
    }

    public static void setIdUsuarioLogado(String idUsuarioLogado) {
        GettersSetters.idUsuarioLogado = idUsuarioLogado;
    }

    public static String getCodigoVendedor() {
        return codigoVendedor;
    }

    public static void setCodigoVendedor(String codigoVendedor) {
        GettersSetters.codigoVendedor = codigoVendedor;
    }

    public static String getFilial() {
        return filial;
    }

    public static void setFilial(String filial) {
        GettersSetters.filial = filial;
    }

    public static String getDescrFilial() {
        return descrFilial;
    }

    public static void setDescrFilial(String descrFilial) {
        GettersSetters.descrFilial = descrFilial;
    }

    public static String getNomeVend() {
        return nomeVend;
    }

    public static void setNomeVend(String nomeVend) {
        GettersSetters.nomeVend = nomeVend;
    }

    public static String getStringConexao() {
        return stringConexao;
    }

    public static void setStringConexao(String stringConexao) {
        GettersSetters.stringConexao = stringConexao;
    }

    public static String getClienteBlq() {
        return clienteBlq;
    }

    public static void setClienteBlq(String clienteBlq) {
        GettersSetters.clienteBlq = clienteBlq;
    }

    public static String getConexaoBD() {
        return conexaoBD;
    }

    public static void setConexaoBD(String conexaoBD) {
        GettersSetters.conexaoBD = conexaoBD;
    }

//    public static Boolean getValidaConexaoBD() {
//        return validaConexaoBD;
//    }
//
//    public static void setValidaConexaoBD(Boolean validaConexaoBD) {
//        GettersSetters.validaConexaoBD = validaConexaoBD;
//    }

    public static String getNumeroColeta() {
        return numeroColeta;
    }

    public static void setNumeroColeta(String numeroColeta) {
        GettersSetters.numeroColeta = numeroColeta;
    }

    public static String getDocCli() {
        return docCli;
    }

    public static void setDocCli(String docCli) {
        GettersSetters.docCli = docCli;
    }

    public static String getCodCli() {
        return codCli;
    }

    public static void setCodCli(String codCli) {
        GettersSetters.codCli = codCli;
    }

    public static String getLojaCli() {
        return lojaCli;
    }

    public static void setLojaCli(String lojaCli) {
        GettersSetters.lojaCli = lojaCli;
    }

    public static String getRzSocialCli() {
        return rzSocialCli;
    }

    public static void setRzSocialCli(String rzSocialCli) {
        GettersSetters.rzSocialCli = rzSocialCli;
    }

    public static String getInscrCli() {
        return inscrCli;
    }

    public static void setInscrCli(String inscrCli) {
        GettersSetters.inscrCli = inscrCli;
    }

    public static String getEmailCli() {
        return emailCli;
    }

    public static void setEmailCli(String emailCli) {
        GettersSetters.emailCli = emailCli;
    }

    public static String getEndEntregaCli() {
        return endEntregaCli;
    }

    public static void setEndEntregaCli(String endEntregaCli) {
        GettersSetters.endEntregaCli = endEntregaCli;
    }

    public static String getBairroEntregaCli() {
        return bairroEntregaCli;
    }

    public static void setBairroEntregaCli(String bairroEntregaCli) {
        GettersSetters.bairroEntregaCli = bairroEntregaCli;
    }

    public static String getMunicEntregaCli() {
        return municEntregaCli;
    }

    public static void setMunicEntregaCli(String municEntregaCli) {
        GettersSetters.municEntregaCli = municEntregaCli;
    }

    public static String getEstEntregaCli() {
        return estEntregaCli;
    }

    public static void setEstEntregaCli(String estEntregaCli) {
        GettersSetters.estEntregaCli = estEntregaCli;
    }

    public static String getCepEntregaCli() {
        return cepEntregaCli;
    }

    public static void setCepEntregaCli(String cepEntregaCli) {
        GettersSetters.cepEntregaCli = cepEntregaCli;
    }

    public static String getDddEntregaCli() {
        return dddEntregaCli;
    }

    public static void setDddEntregaCli(String dddEntregaCli) {
        GettersSetters.dddEntregaCli = dddEntregaCli;
    }

    public static String getFoneEntregaCli() {
        return foneEntregaCli;
    }

    public static void setFoneEntregaCli(String foneEntregaCli) {
        GettersSetters.foneEntregaCli = foneEntregaCli;
    }

    public static String getEndCobranCli() {
        return endCobranCli;
    }

    public static void setEndCobranCli(String endCobranCli) {
        GettersSetters.endCobranCli = endCobranCli;
    }

    public static String getBairroCobranCli() {
        return bairroCobranCli;
    }

    public static void setBairroCobranCli(String bairroCobranCli) {
        GettersSetters.bairroCobranCli = bairroCobranCli;
    }

    public static String getMunicCobranCli() {
        return municCobranCli;
    }

    public static void setMunicCobranCli(String municCobranCli) {
        GettersSetters.municCobranCli = municCobranCli;
    }

    public static String getEstCobranCli() {
        return estCobranCli;
    }

    public static void setEstCobranCli(String estCobranCli) {
        GettersSetters.estCobranCli = estCobranCli;
    }

    public static String getCepCobranCli() {
        return cepCobranCli;
    }

    public static void setCepCobranCli(String cepCobranCli) {
        GettersSetters.cepCobranCli = cepCobranCli;
    }

    public static String getDddCobranCli() {
        return dddCobranCli;
    }

    public static void setDddCobranCli(String dddCobranCli) {
        GettersSetters.dddCobranCli = dddCobranCli;
    }

    public static String getFoneCobranCli() {
        return foneCobranCli;
    }

    public static void setFoneCobranCli(String foneCobranCli) {
        GettersSetters.foneCobranCli = foneCobranCli;
    }

    public static String getTipoVendedor() {
        return tipoVendedor;
    }

    public static void setTipoVendedor(String tipoVendedor) {
        GettersSetters.tipoVendedor = tipoVendedor;
    }

    public static boolean isIsOrcamento() {
        return isOrcamento;
    }

    public static void setIsOrcamento(boolean isOrcamento) {
        GettersSetters.isOrcamento = isOrcamento;
    }

    public static void resetGettersSetters() {
        horaLanc = "";
        filial = "";
        descrFilial = "";
        clienteBlq = "";
        seqIdenficador = "";
        numeroColeta = "";
        isSessionFoto = false;
        isSessionAssinat = false;
        isLogin = false;

        // Daods cliente
        docCli = "";
        codCli = "";
        lojaCli = "";
        rzSocialCli = "";
        inscrCli = "";
        emailCli = "";
        endEntregaCli = "";
        bairroEntregaCli = "";
        municEntregaCli = "";
        estEntregaCli = "";
        cepEntregaCli = "";
        dddEntregaCli = "";
        foneEntregaCli = "";
        endCobranCli = "";
        bairroCobranCli = "";
        municCobranCli = "";
        estCobranCli = "";
        cepCobranCli = "";
        dddCobranCli = "";
        foneCobranCli = "";
        validarDadosCli = true;
        descExclusivo = 0;
        clienteGrp = false;
        descGrupo = 0;

        // Coleta
        valorColeta = "";
        qtdItensColeta = "";
        categoriaCli = "";
        tabelaPrcCli = "";

        // Variáveis condições de pagamento
        codCondPgto = "";
        condPgto = "";
        descrCondPgto = "";
        descrFormaPgto = "";

        // Variávels Borracheiros
        tipoColeta = "";
        descrTipoColeta = "";
        checkComissBorr = false;
        isOrcamento = false;
        isBorracheiroCli = false; //ALTERAÇÃO CMD 959
        isBorracheiroVendPat = false;
        pathAssinBorr = null;
        picNameAssinBorr = "";
        nomeBorracheiro = "";
        codBorracheiro = "";
        docBorracheiro = "";
        infoAdicionais = "";
        isBorracheiro = false;

        // Variável aceite termos
        aceiteTermos = false;
        tipoAssinatura = "";

        // Assinatura Cliente
        pathAssinCli = null;
        picNameAssinCli = "";
        referenciasComerciais = "";

        arrItemColeta.clear();
        arrQtdItemColeta.clear();
        arrCodProdColeta.clear();
        arrBitolaColeta.clear();
        arrMarcaColeta.clear();
        arrModeloColeta.clear();
        arrSerieColeta.clear();
        arrDotColeta.clear();
        arrMontadoColeta.clear();
        arrDesenhoColeta.clear();
        arrValorUnit.clear();
        arrUrgenteColeta.clear();
        arrPorcComisBorr.clear();
        arrValrComisBorr.clear();
        arrObsItens.clear();
        arrCAgua.clear();
        arrCCamara.clear();
        arrGarantia.clear();
        arrVlrBlqIt.clear();
        arrBaseCalcIt.clear();
        arrFilialOri.clear();
        arrColetaOri.clear();
        arrItemOri.clear();

        tipoFoto = "";
        tipoDoc = "";
        docsNovoCliOK = true;
        pathDocsNovoCli = "";
        picNameRGCli = "";
        picNameDocCli = "";
        picNameComprResidCli = "";
        fotoCapturada = null;
        enviarColeta = false;
        envioColetaBDExt = false;
        erroEnvioColetaBDExt = "";
        erroSalvarColetaBDInt = "";
        isNumeroInserido = false;
        expSqLite = "";
        isCfgEmailOk = false;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDataBR() {
        Date date = new Date();
        DateFormat dataPtBR = new SimpleDateFormat("dd/MM/yyyy");
        return dataPtBR.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDataEN() {
        Date date = new Date();
        DateFormat dataPtBR = new SimpleDateFormat("yyyyMMdd");
        return dataPtBR.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getHora() {
        SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
        Date hora = Calendar.getInstance().getTime();
        return horaFormat.format(hora);
    }

    @SuppressLint("SimpleDateFormat")
    public static String converteData(String dataConv, String formatoConversao) {
        DateFormat formatDataInput;
        DateFormat formatDataOutput;

        if (formatoConversao.equals("EN")) {
            formatDataInput = new SimpleDateFormat("yyyy-MM-dd");
            formatDataOutput = new SimpleDateFormat("dd/MM/yyyy");
        } else {
            formatDataInput = new SimpleDateFormat("dd/MM/yyyy");
            formatDataOutput = new SimpleDateFormat("yyyyMMdd");
        }
        try {
            Date data = formatDataInput.parse(dataConv);
            assert data != null;
            dataConv = formatDataOutput.format(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dataConv;
    }

    public static String calendarDataSubtr(String data, int dias) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(data)));
            calendar.add(Calendar.DATE, dias); //SUBTRAI ou SOMA x DIAS
            Date date = new Date(calendar.getTimeInMillis());
            data = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return data;
    }
}

