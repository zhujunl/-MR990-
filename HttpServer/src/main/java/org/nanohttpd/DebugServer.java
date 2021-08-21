package org.nanohttpd;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * @author Tank
 * @date 2021/8/3 8:37 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class DebugServer extends NanoHTTPD {

    private final String TAG = "DebugServer";
    private Gson mGson = new Gson();

    public DebugServer() {
        super(8081);
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session != null) {
            Map<String, List<String>> parameters = session.getParameters();
            return NanoHTTPD.newFixedLengthResponse( mGson.toJson(MxResponse.CreateSuccess(parameters)));
        } else {
            return NanoHTTPD.newFixedLengthResponse(
                    Response.Status.NO_CONTENT, NanoHTTPD.MIME_PLAINTEXT, mGson.toJson(MxResponse.CreateFail()));
        }
        //        Map<String, List<String>> decodedQueryParameters = decodeParameters(session.getQueryParameterString());
        //        StringBuilder sb = new StringBuilder();
        //        sb.append("<html>");
        //        sb.append("<head><title>Debug Server</title></head>");
        //        sb.append("<body>");
        //        sb.append("<h1>Debug Server</h1>");
        //
        //        sb.append("<p><blockquote><b>URI</b> = ").append(
        //                String.valueOf(session.getUri())).append("<br />");
        //
        //        sb.append("<b>Method</b> = ").append(
        //                String.valueOf(session.getMethod())).append("</blockquote></p>");
        //
        //        sb.append("<h3>Headers</h3><p><blockquote>").
        //                append(toString(session.getHeaders())).append("</blockquote></p>");
        //
        //        sb.append("<h3>Parms</h3><p><blockquote>").
        //                append(toString(session.getParms())).append("</blockquote></p>");
        //
        //        sb.append("<h3>Parms (multi values?)</h3><p><blockquote>").
        //                append(toString(decodedQueryParameters)).append("</blockquote></p>");
        //
        //        try {
        //            Map<String, String> files = new HashMap<String, String>();
        //            session.parseBody(files);
        //            sb.append("<h3>Files</h3><p><blockquote>").
        //                    append(toString(files)).append("</blockquote></p>");
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        //
        //        sb.append("</body>");
        //        sb.append("</html>");
        //        //        Response response = new Response(sb.toString());
        //        //        return response;
        //        return NanoHTTPD.newFixedLengthResponse(sb.toString());
    }

    private String toString(Map<String, ? extends Object> map) {
        if (map.size() == 0) {
            return "";
        }
        return unsortedList(map);
    }

    private String unsortedList(Map<String, ? extends Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            listItem(sb, entry);
        }
        sb.append("</ul>");
        return sb.toString();
    }

    private void listItem(StringBuilder sb, Map.Entry<?, ?> entry) {
        sb.append("<li><code><b>").append(entry.getKey()).
                append("</b> = ").append(entry.getValue()).append("</code></li>");
    }
}
