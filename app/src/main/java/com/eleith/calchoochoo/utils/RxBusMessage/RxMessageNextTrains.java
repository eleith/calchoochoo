package com.eleith.calchoochoo.utils.RxBusMessage;

import android.support.v4.util.Pair;

import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;

import java.util.ArrayList;

public class RxMessageNextTrains extends RxMessage<ArrayList<PossibleTrain>>{

  public RxMessageNextTrains(String type, ArrayList<PossibleTrain> possibleTrains) {
    super(type, possibleTrains);
  }
}
