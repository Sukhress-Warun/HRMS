package filterUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class FilterHelper {


    public static String QUERY_PARAM_FILTERS = "queryParamFilters";
    public static String BODY_FIELD_FILTERS = "bodyFieldFilters";
    public static String PATH_PARAM_FILTERS = "pathParamFilters";

    private HttpServletRequest request;

    public FilterHelper(HttpServletRequest request){
        this.request = request;
        init();
    }

    private void init(){
        if(request.getAttribute(QUERY_PARAM_FILTERS) == null){
            request.setAttribute(QUERY_PARAM_FILTERS, new LinkedHashMap<String, Set<String>>());
        }
        if(request.getAttribute(BODY_FIELD_FILTERS) == null){
            request.setAttribute(BODY_FIELD_FILTERS, new LinkedHashMap<String, Set<String>>());
        }
        if(request.getAttribute(PATH_PARAM_FILTERS) == null){
            request.setAttribute(PATH_PARAM_FILTERS, new LinkedHashMap<String, Set<Integer>>());
        }
    }

    public void addQueryFilterField(String filterName, String fieldName){
        LinkedHashMap<String, Set<String>> queryParamFilters = (LinkedHashMap)request.getAttribute(QUERY_PARAM_FILTERS);
        Set<String> fields = queryParamFilters.getOrDefault(filterName, new HashSet<>());
        fields.add(fieldName);
        queryParamFilters.put(filterName, fields);
    }

    public void addBodyFilterField(String filterName, String fieldName){
        LinkedHashMap<String, Set<String>> bodyFieldFilters = (LinkedHashMap)request.getAttribute(BODY_FIELD_FILTERS);
        Set<String> fields = bodyFieldFilters.getOrDefault(filterName, new HashSet<>());
        fields.add(fieldName);
        bodyFieldFilters.put(filterName, fields);
    }

    public void addPathFilterPosition(String filterName, Integer fieldPosition){
        LinkedHashMap<String, Set<Integer>> pathParamFilters = (LinkedHashMap)request.getAttribute(PATH_PARAM_FILTERS);
        Set<Integer> positions = pathParamFilters.getOrDefault(filterName, new HashSet<>());
        positions.add(fieldPosition);
        pathParamFilters.put(filterName, positions);
    }

    public Set<String> getQueryFilterFields(String filterName){
        LinkedHashMap<String, Set<String>> queryParamFilters = (LinkedHashMap)request.getAttribute(QUERY_PARAM_FILTERS);
        return queryParamFilters.getOrDefault(filterName, new HashSet<>());
    }

    public Set<String> getBodyFilterFields(String filterName){
        LinkedHashMap<String, Set<String>> bodyFieldFilters = (LinkedHashMap)request.getAttribute(BODY_FIELD_FILTERS);
        return bodyFieldFilters.getOrDefault(filterName, new HashSet<>());
    }

    public Set<Integer> getPathFilterPositions(String filterName){
        LinkedHashMap<String, Set<Integer>> pathParamFilters = (LinkedHashMap)request.getAttribute(PATH_PARAM_FILTERS);
        return pathParamFilters.getOrDefault(filterName, new HashSet<>());
    }

}
