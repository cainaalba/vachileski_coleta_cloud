package pneus.vachileski_mobi_funcoes_genericas;

import android.annotation.SuppressLint;
import android.content.Context;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressLint({"WifiManagerPotentialLeak", "HardwareIds", "SimpleDateFormat"})
public class GeraToken {
    private final ConexaoBDExt conexaoBDExt = new ConexaoBDExt();

    public String solicitaToken(String usuario, String whats, Context context, String token) {
        String tokenValido = null;
        conexaoBDExt.ConnectToDatabase("C", GettersSetters.getConexaoBD());
//        conexaoBDExt.buscaToken(null, null, getMacAddr(), usuario);
//
//        if (conexaoBDExt.arrToken.size() == 0) {
//            conexaoBDExt.ConnectToDatabase("C", GettersSetters.getConexaoBD());
//            conexaoBDExt.insereToken("000", getMacAddr(), usuario, whats, getData(), getHora(), "", "", "", "", "", "", GettersSetters.getTipoUsuario());
//            tokenValido = null;
//        } else {
//            for (int i = 0; i < conexaoBDExt.arrToken.size(); i++) {
//                tokenValido = conexaoBDExt.arrToken.get(i);
//            }
//        }
        return tokenValido;
    }

    public String geraToken() {
        String alphaNumerico = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ.-#@";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            sb.append(alphaNumerico.charAt(secureRandom.nextInt(alphaNumerico.length())));
        }
        return sb.toString();
    }

    public static String getData() {
        DateFormat formatDataEn = new SimpleDateFormat("yyyyMMdd");
        Date dataEng = new Date();
        return formatDataEn.format(dataEng);
    }

    public static String getHora() {
        SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
        Date hora = Calendar.getInstance().getTime();
        return horaFormat.format(hora);
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
