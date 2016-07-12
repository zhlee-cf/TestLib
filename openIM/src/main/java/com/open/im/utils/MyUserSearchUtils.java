package com.open.im.utils;

import com.open.im.app.MyApp;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.List;

public class MyUserSearchUtils {

    /**
     * 方法 根据用户名 查找用户
     *
     * @param username
     * @return
     */
    public static List<Row> searchUsers(String username) {
        List<Row> list = null;
        // 获取用户搜查者对象
        try {
            UserSearchManager usm = new UserSearchManager(MyApp.connection);
            String serviceName = MyConstance.SERVICE_HOST;
            // 获得要查询的表格 参数为vjud. + 服务名称
            Form searchForm = usm.getSearchForm("vjud." + serviceName);
            // 根据要查询的表创建一个新的表格 填充查询出来的数据
            Form answerForm = searchForm.createAnswerForm();
            List<FormField> fields = searchForm.getFields();
            for (FormField field : fields) {
                MyLog.showLog("field::" + field.getVariable());
            }
            // 设置要查询的字段 和 查询的依据
            answerForm.setAnswer("user", username);
            // 查询
            ReportedData data = usm.getSearchResults(answerForm, "vjud." + serviceName);
            // 获取查出来的行
            list = data.getRows();
        } catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
            e.printStackTrace();
        }
        return list;
    }
}
