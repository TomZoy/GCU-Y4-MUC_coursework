package com.gcu.zoltantompa.geocoral;


import java.util.ArrayList;

/**
 *  custom interface to enable call-back from the implementing/calling class
 **/

public interface pcHttpJSONAsyncInterface {
    void onResponseReceived(Object resultMap, ArrayList<EarthQ> resultObjList);
}
