package com.eleith.calchoochoo.utils.RxBusMessage;

import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;

import java.util.ArrayList;

public class RxMessageRoutes extends RxMessage<ArrayList<Routes>>{
  public RxMessageRoutes(String key, ArrayList<Routes> routes) {
    super(key, routes);
  }
}
