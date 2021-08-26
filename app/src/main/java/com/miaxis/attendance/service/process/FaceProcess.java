package com.miaxis.attendance.service.process;

import com.miaxis.attendance.data.model.FaceModel;
import com.miaxis.attendance.service.MxResponse;
import com.miaxis.attendance.service.process.base.GetParamProcess;

import java.util.Map;


/**
 * @author Tank
 * @date 2021/8/23 5:28 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FaceProcess {


    public static class QueryAllFace extends GetParamProcess {
        public QueryAllFace() {
        }

        @Override
        protected MxResponse<?> onPostParamProcess(Map<String, String> parameter) throws Exception {
            return MxResponse.CreateSuccess(FaceModel.findAll());
        }
    }

}



