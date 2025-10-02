package com.bappul.delivery.payment.application.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

  private final IamportClient importClient;

  public void cancelPayment(String impUid){
    try {
      CancelData cancelData = new CancelData(impUid, true);
      importClient.cancelPaymentByImpUid(cancelData);
    } catch (IamportResponseException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void fakeCancelPayment(String impUid){
    // TODO 환불 로직
    log.info("[ Payment - RefundService ] Fake 환불 로직 수행 {}", impUid);
  }
}
