package com.open.im.bean;

/**
 * Created by Administrator on 2016/4/19.
 */
public class ResultBean extends ProtocalObj {

    /**
     * namespace : http://oim.daimaqiao.net/openstore_1.0
     * version : 1.0.1
     * apicall : putimage
     * error :
     * result : http://openim.top:8080/openstore/api/getimage.php?fileid=Z3JvdXAxL00wMC8wMC8wMC9vWVlCQUZjVnZtV0FZRER3QUFZVk5tdWY1blEwMTAucG5n&oim=eyJuYW1lc3BhY2UiOiJodHRwOi8vb2ltLmRhaW1hcWlhby5uZXQvb3BlbmltLjEuMCIsInZlcnNpb24iOiIxLjAuMCIsInR5cGUiOiJpbWFnZSIsInNpemUiOjM5ODY0NiwicHJvcGVydGllcyI6eyJyZXNvbHV0aW9uIjoiOTgweDEyMDAiLCJ0aHVtYm5haWwiOiJodHRwOi8vb3BlbmltLmRhaW1hcWlhby5uZXQ6ODA4MC9vcGVuc3RvcmUvYXBpL2dldGltYWdlLnBocD9maWxlaWQ9WjNKdmRYQXhMMDB3TUM4d01DOHdNQzl2V1ZsQ1FVWmpWblp0VjBGWlJFUjNRVUZaVms1dGRXWTFibEV3TVRBdGRHaDFiV0p1WVdsc0xuQnVadz09In19
     */

    private String namespace;
    private String version;
    private String apicall;
    private String error;
    private String result;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApicall() {
        return apicall;
    }

    public void setApicall(String apicall) {
        this.apicall = apicall;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "namespace='" + namespace + '\'' +
                ", version='" + version + '\'' +
                ", apicall='" + apicall + '\'' +
                ", error='" + error + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
