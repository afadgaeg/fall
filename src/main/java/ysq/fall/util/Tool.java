package ysq.fall.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import ysq.fall.FallException;

public class Tool {

    static String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i"
            + "|windows (phone|ce)|blackberry"
            + "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"
            + "|laystation portable)|nokia|fennec|htc[-_]"
            + "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
    static String tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser"
            + "|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

    static Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
    static Pattern tablePat = Pattern.compile(tableReg, Pattern.CASE_INSENSITIVE);
    private static final Browser browser = new Browser();

//    public static Document getDocument(String url) throws IOException, InterruptedException, URISyntaxException {
//        return getDocument(url, "UTF-8", 5);
//    }
//    public static Document getDocument(String url, String encode, int time) throws IOException, InterruptedException, URISyntaxException {
//        String data = Tool.getData(url, encode, time);
//        if (data == null) {
//            return null;
//        }
//        return Jsoup.parse(data);
//    }
//    public static String getData(String url) throws IOException, InterruptedException, URISyntaxException {
//        return getData(url, null, "UTF-8", 5);
//    }
//
//    public static String getData(String url, String encode, int time) throws IOException, InterruptedException, URISyntaxException {
//        return getData(url, null, encode, 5);
//    }
//
//    public static String getData(String url, String userAgent, String encode, int time) throws IOException, InterruptedException, URISyntaxException {
//        return getData(url, userAgent, encode, time, 1000);
//    }
//    public static String getData(String url, String userAgent, String encode, int time, long slp) throws IOException, InterruptedException, URISyntaxException {
//
//        for (int i = 0; i < time - 1; i++) {
//            try {
//                String data = _getData(url, userAgent, encode, slp);
//                if (data != null) {
//                    return data;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return _getData(url, userAgent, encode, slp);
//    }
//    private static String _getData(String url, String userAgent, String encode, long slp) throws IOException, InterruptedException {
//        try (CloseableHttpClient client = userAgent == null ? HttpClientBuilder.create().build()
//                : HttpClientBuilder.create().setUserAgent(userAgent).build();) {
//            HttpResponse httpResponse = getHttpResponse(client, url, userAgent);
//
//            if (slp > 0) {
//                Thread.sleep(slp);
//            }
//            int statusCode = httpResponse.getStatusLine().getStatusCode();
//            if (statusCode != 200) {
//                System.out.println("http请求返回非200状态码：" + statusCode);
//                return null;
//            }
//            HttpEntity entity = httpResponse.getEntity();
//            if (entity == null) {
//                System.out.println("http请求返回实体内容为空");
//                return null;
//            }
//            try (InputStream in = entity.getContent()) {
//                return IOUtils.toString(in, encode).trim();
//            }
//        }
//
//    }
//    public static HttpResponse getHttpResponse(CloseableHttpClient client, String url, String userAgent) throws IOException, InterruptedException {
//
//        HttpGet httpGet = new HttpGet(url);
////            if (userAgent != null) {
////                httpGet.setHeader(HttpHeaders.USER_AGENT, userAgent);
////            }
//        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5 * 1000)
//                .setConnectTimeout(10 * 1000).setConnectionRequestTimeout(2 * 1000).build();
//        httpGet.setConfig(requestConfig);
//        return client.execute(httpGet);
//
//    }
    static public String encode(String s, boolean isAnalytic) {
        return encode(s, isAnalytic, "");
    }

    static public String encode(String s, boolean isAnalytic, String prefix) {
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("[\\u4E00-\\u9FA5]+|[a-zA-Z0-9]+");
        Matcher m = p.matcher(s.trim());
        while (m.find()) {
            String str = m.group();
            char[] cs = str.toCharArray();
            char c = cs[0];
            StringBuilder sb2 = new StringBuilder();
            if (c < 19968 || c > 40869) {
                sb2.append(" ").append(prefix).append(str);
            } else if (isAnalytic && cs.length != 1) {
                for (int i = 0; i < cs.length - 1; i++) {
                    sb2.append(" ").append(prefix).append(Integer.toHexString(cs[i])).append(Integer.toHexString(cs[i + 1]));
                }
            } else {
                sb2.append(" ").append(prefix);
                for (char cr : cs) {
                    sb2.append(Integer.toHexString(cr));
                }
            }
            m.appendReplacement(sb, sb2.toString());
        }
        m.appendTail(sb);
        return sb.toString().trim();
    }

    public static void sendMail(String smtp, final String username, final String password,
            InternetAddress from, InternetAddress[] to, String subject, String content)
            throws NoSuchProviderException, MessagingException {
        Properties props = new Properties();
//        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", smtp);
//        props.put("mail.smtp.port", 25);
        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.connectiontimeout", 180);
//	props.put("mail.smtp.timeout", 600);

        Session mailSession = Session.getInstance(props, new Authenticator() {

            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        MimeMessage mimeMsg = new MimeMessage(mailSession);

        mimeMsg.setFrom(from);

        mimeMsg.setRecipients(MimeMessage.RecipientType.TO, to);
        mimeMsg.setSubject(subject, "utf-8");
        mimeMsg.setContent(content, "text/html;charset=utf-8");
//        Multipart multipart = new MimeMultipart();//附件传输格式
//        MimeBodyPart messageBodyPart1 = new MimeBodyPart();
//        //messageBodyPart.setText(UnicodeToChinese(text));
//        messageBodyPart1.setContent(content, "text/html;charset=utf-8");
//        multipart.addBodyPart(messageBodyPart1);
//         for (int i = 0; i < filenames.length; i++) {
//             MimeBodyPart messageBodyPart2 = new MimeBodyPart();
//             String filename = filenames[i].split(",")[0];           //选择出每一个附件名
//             String displayname = filenames[i].split(",")[1];
//             FileDataSource fds = new FileDataSource(filename);      //得到数据源
//             //得到附件本身并至入BodyPart
//             messageBodyPart2.setDataHandler(new DataHandler(fds));
//             //得到文件名同样至入BodyPart
//             //messageBodyPart2.setFileName(displayname);
//             //messageBodyPart2.setFileName(fds.getName());
//             messageBodyPart2.setFileName(MimeUtility.encodeText(displayname));
//             multipart.addBodyPart(messageBodyPart2);
//         }
//        mimeMsg.setContent(multipart);
        mimeMsg.setSentDate(new Date());
        mimeMsg.saveChanges();
        Transport transport = mailSession.getTransport("smtp");
//        mailSession.setDebug(true);
        Transport.send(mimeMsg);
        transport.close();
    }

    public static String getDwz(String url) throws IOException, InterruptedException, Exception {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost("http://dwz.cn/create.php");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(2 * 1000)
                    .setConnectTimeout(5 * 1000)
                    .setConnectionRequestTimeout(500)
                    .build();
            httpPost.setConfig(requestConfig);

            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("url", url));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            HttpResponse httpResponse = client.execute(httpPost);
            Thread.sleep(1000);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                System.out.println("dwz服务器返回非200状态码：" + statusCode);
                return null;
            }
            HttpEntity entity = httpResponse.getEntity();
            if (entity == null) {
                System.out.println("dwz服务器返回实体内容为空");
                return null;
            }
            try (InputStream in = entity.getContent()) {
                String body = IOUtils.toString(in, "UTF-8").trim();
                JSONObject json = JSON.parseObject(body);
                int status = json.getInteger("status");
                if (status != 0) {
                    throw new Exception("请求dwz短网址失败");
                }

                return json.getString("tinyurl").trim();
            }
        }
    }

    static public Date randomDate(String beginDate, String endDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date start = format.parse(beginDate);// 开始日期
        Date end = format.parse(endDate);// 结束日期
        if (start.getTime() >= end.getTime()) {
            return null;
        }
        long date = randomLong(start.getTime(), end.getTime());
        return new Date(date);
    }

    static private long randomLong(long begin, long end) {
        long rtnn = begin + (long) (Math.random() * (end - begin));
        if (rtnn == begin || rtnn == end) {
            return randomLong(begin, end);
        }
        return rtnn;
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度  
        String base = "abcdefghijklmnopqrstuvwxyz";
        Random random = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min) + min;
    }

    public static int getRandomInt(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    public static void copy2Clipboard(String str) {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(str);
        sysClip.setContents(tText, null);
    }

    public static void writeString2File(String content, String file, boolean append) throws IOException {
        writeString2File(content, new File(file), append);
    }

    public static void writeString2File(String content, String file) throws IOException {
        writeString2File(content, file, false);
    }

    public static void writeString2File(String content, File file, boolean append) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file, append);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write(content);
        }
    }

    public static void writeString2File(String content, File file) throws IOException {
        writeString2File(content, file, false);
    }

    public static String getSql(PreparedStatement stmt) {
        return stmt.toString().substring(stmt.toString().indexOf(":") + 1).trim();
    }

    static public void playPrompt() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String u = "file:///"+getJarPath() + "audio/1.wav";
                    URL url = new URL(u);
                    Player player = Manager.createPlayer(new MediaLocator(url));
                    player.start();
                    int i = 15;
                    while (player.getState() != 500 && i > 0) {
                        i--;
                        Thread.sleep(2000);
                    }
                    player.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    static public String getJarPath() {
        String filePath = System.getProperty("java.class.path");
        String pathSplit = System.getProperty("path.separator");//得到当前操作系统的分隔符，windows下是";",linux下是":" 
        /**
         * 若没有其他依赖，则filePath的结果应当是该可运行jar包的绝对路径， 此时我们只需要经过字符串解析，便可得到jar所在目录
         */
        if (filePath.contains(pathSplit)) {
            filePath = filePath.substring(0, filePath.indexOf(pathSplit));
        } else if (filePath.endsWith(".jar")) {
//截取路径中的jar包名,可执行jar包运行的结果里包含".jar" 
            filePath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);
        }
        return filePath;
    }

    public static String readFile2String(File file, String charset) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        try (FileInputStream fileIs = new FileInputStream(file);
                InputStreamReader ips = new InputStreamReader(fileIs, charset);
                BufferedReader reader = new BufferedReader(ips);) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    public static String blankNull(Object o) {
        if (o == null) {
            return "";
        }
        return o.toString();
    }

    public static String getRootPath(HttpServletRequest request) {
        String serverPort = request.getServerPort() == 80 ? "" : ":" + request.getServerPort();
        String rootPath = request.getScheme() + "://" + request.getServerName() + serverPort + request.getContextPath();
        return rootPath;
    }

    public static String getMD5(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] inputByteArray = input.getBytes("utf-8");
        messageDigest.update(inputByteArray);
        byte[] resultByteArray = messageDigest.digest();
        return byteArrayToHex(resultByteArray);
    }

    private static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    public static boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT").toLowerCase();
        if (null == userAgent) {
            userAgent = "";
        }
        Matcher matcherPhone = phonePat.matcher(userAgent);
        Matcher matcherTable = tablePat.matcher(userAgent);
        return matcherPhone.find() || matcherTable.find();
    }

    static public String trim(String textContent) {
        textContent = textContent.trim();
        while (textContent.startsWith("　")) {//这里判断是不是全角空格
            textContent = textContent.substring(1, textContent.length()).trim();
        }
        while (textContent.endsWith("　")) {
            textContent = textContent.substring(0, textContent.length() - 1).trim();
        }
        while (textContent.startsWith(" ")) {//这里判断是不是全角空格
            textContent = textContent.substring(1, textContent.length()).trim();
        }
        while (textContent.endsWith(" ")) {
            textContent = textContent.substring(0, textContent.length() - 1).trim();
        }
        return textContent;
    }

    //-------------------------------------
    public static String getData(String url, String encode, int maxTime, int interval)
            throws IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String data = null;
        for (int i = 0; i < maxTime; i++) {
            try {
                if (interval > 0) {
                    Thread.sleep(interval);
                }
                data = getData(url, encode);
                break;
            } catch (Exception e) {
                if (i == maxTime - 1) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
        return data;
    }

    public static String getData(String url, String encode) throws IOException, InterruptedException,
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        try (CloseableHttpClient client = browser.getHttpClient();) {
            HttpResponse res = browser.doGet(client, url);
            if (res.getStatusLine().getStatusCode() == 200) {
                try (final InputStream in = res.getEntity().getContent()) {
                    return IOUtils.toString(in, encode).trim();
                }
            } else if (res.getStatusLine().getStatusCode() == 404) {
                return null;
            } else {
                throw new FallException("################### return " + res.getStatusLine().getStatusCode() + ":" + url);
            }
        }
    }

    public static HttpClientReturn getHttpClientReturn(String url) throws IOException, InterruptedException,
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return getHttpClientReturn(url, "UTF-8");
    }

    public static HttpClientReturn getHttpClientReturn(String url, String encode) throws IOException, InterruptedException,
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        try (CloseableHttpClient client = browser.getHttpClient();) {
            HttpResponse res = browser.doGet(client, url);
            StatusLine statusLine = res.getStatusLine();
            Integer statusCode = statusLine.getStatusCode();
            HttpClientReturn re = new HttpClientReturn();
            re.setStatusCode(statusCode);
            re.setStatusLine(statusLine.toString());
            if (statusCode == 200) {
                try (final InputStream in = res.getEntity().getContent()) {
                    re.setContent(IOUtils.toString(in, encode).trim());
                }
            }
            return re;
        }
    }

    public static HttpClientReturn postHttpClientReturn(String url, List<NameValuePair> postParameters) throws IOException, InterruptedException,
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return postHttpClientReturn(url, postParameters, "UTF-8");
    }

    public static HttpClientReturn postHttpClientReturn(String url, List<NameValuePair> postParameters, String encode)
            throws IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        try (CloseableHttpClient client = browser.getHttpClient();) {
            HttpResponse res = browser.doPost(client, url, postParameters, encode);
            StatusLine statusLine = res.getStatusLine();
            Integer statusCode = statusLine.getStatusCode();
            HttpClientReturn re = new HttpClientReturn();
            re.setStatusCode(statusCode);
            re.setStatusLine(statusLine.toString());
            if (statusCode == 200) {
                try (final InputStream in = res.getEntity().getContent()) {
                    re.setContent(IOUtils.toString(in, encode).trim());
                }
            }
            return re;
        }
    }

    //-------------------------------------------------
    public static List<String> addToList(List<String> list, List<String> addList) {
        if (addList != null && list != null) {
            for (String item : addList) {
                addToList(list, item);
            }
        }
        return list;
    }

    public static List<String> addToList(List<String> list, String item) {
        if (item != null) {
            item = item.toLowerCase().trim();
            if (!item.isEmpty() && !list.contains(item)) {
                list.add(item);
            }
        }
        return list;
    }

    public static String listToStr(List<String> list) {
        String str = "";
        if (!list.isEmpty()) {
            for (String it : list) {
                str += "/" + it.toLowerCase().trim();
            }
            if (!str.isEmpty()) {
                str = str.substring(1);
            }
        }
        return str.toLowerCase().trim();
    }

    public static List<String> strToList(String listStr) {
        List<String> list = new ArrayList<>();
        if (listStr != null && !listStr.trim().isEmpty()) {
            for (String item : listStr.toLowerCase().trim().split("\\s*/\\s*")) {
                item = item.toLowerCase().trim();
                if (!list.contains(item)) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    public static String cutQS(String url) {
        String[] strArray = url.trim().split(Pattern.quote("?"));
        String qs = "";
        for (String kv : strArray[1].trim().split(Pattern.quote("&"))) {
            if (kv.matches(".+=.+")) {
                qs += "&" + kv.trim();
            }
        }
        url = strArray[0];
        if (!qs.isEmpty()) {
            url += "?" + qs.substring(1);
        }
        return url;
    }

    //-----------------------------
    public static void bind(ysq.fall.util.URL url, Object bean, String[] paramNames) throws NoSuchFieldException,
            UnsupportedEncodingException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (String paramName : paramNames) {
            Object paramValue = PropertyUtils.getProperty(bean, paramName);
            if (paramValue instanceof Collection) {
                for (Object v : (Collection) paramValue) {
                    if (v != null) {
                        String value = String.valueOf(v).trim();
                        if (!value.isEmpty()) {
                            url.addParam(paramName, value);
                        }
                    }
                }
            } else if (paramValue != null) {
                String value = String.valueOf(paramValue).trim();
                if (!value.isEmpty()) {
                    url.addParam(paramName, value);
                }
            }
        }
    }
}
