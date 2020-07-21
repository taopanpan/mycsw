package com.csw.servlet.util;

import com.alibaba.fastjson.JSONArray;
import com.csw.data.util.GetDetailInfo;
import com.csw.data.util.QueryDataUtil;
import com.csw.model.LocalizedString;
import com.csw.model.RegistryPackage;
import com.string.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "registry")
public class QueryByNameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String name = request.getParameter("name");

        List<LocalizedString> localizedStrings = QueryDataUtil.selectLocalizedStringsByFuzzIdAndValue("%:Name","%"+name+"%");
        Set<String> ids = new HashSet<String>();
        if (localizedStrings.size()>0) {
            for (LocalizedString localizedString : localizedStrings) {
                String id = StringUtil.deleteStr(localizedString.getId(),":Name");
                List<RegistryPackage> registryPackages = QueryDataUtil.selectRegistryPackageById(StringUtil.appendPackage(id));
                if (registryPackages.size()>0) {
                    ids.add(id);
                }
            }
        }

        Set<String> _ids = new HashSet<String>();
        for (String id : ids) {
            String[] envelope = GetDetailInfo.getEnvelope(id);
            String coordinate = envelope[0]+" "+envelope[1];
            if (!(coordinate.equals("none none none none"))) {
                _ids.add(id);
            }
        }

        List<Map<String,String>> results = new ArrayList<Map<String, String>>();
        for (String id : _ids) {
            List<RegistryPackage> registryPackages = QueryDataUtil.selectRegistryPackageById(StringUtil.appendPackage(id));
            for (RegistryPackage registryPackage : registryPackages) {
                Map<String,String> result = new HashMap<String, String>();
                String idTemp = registryPackage.getId();
                String nameTemp = QueryDataUtil.selectLocalizedStringsBy_Id(StringUtil.deletePackage(idTemp)+":Name").get(0).getValue();
                String[] envelope = GetDetailInfo.getEnvelope(registryPackage.getId());
                String coordinate = envelope[0]+" "+envelope[1];

                result.put("id",idTemp);
                result.put("name",nameTemp);
                result.put("time",QueryDataUtil.selectTimeByRegistryPackageId(idTemp));
                result.put("type",registryPackage.getType());
                result.put("envelope",coordinate);
                results.add(result);
            }
        }

        String jsonStr = JSONArray.toJSONString(results);
        PrintWriter pw = response.getWriter();
        pw.write(jsonStr);
        pw.flush();
        pw.close();
    }
}
