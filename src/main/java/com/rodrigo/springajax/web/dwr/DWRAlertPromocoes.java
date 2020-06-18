package com.rodrigo.springajax.web.dwr;

import com.rodrigo.springajax.repository.PromocaoRepository;
import org.directwebremoting.*;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static org.springframework.data.domain.Sort.*;

@RemoteProxy
@Component
public class DWRAlertPromocoes {

    @Autowired
    private PromocaoRepository promocaoRepository;

    private Timer timer;

    private LocalDateTime getDtCadastroByUltimaPromocao() {
        PageRequest pageRequest = PageRequest.of(0,1, Direction.DESC, "dtCadastro"); //traga a data mais recente
        return promocaoRepository.findUltimaDataDePromocao(pageRequest)
                .getContent()
                .get(0);
    }

    @RemoteMethod
    public synchronized void init() {
        System.out.println("DWR está ativado.");

        LocalDateTime lastDate = getDtCadastroByUltimaPromocao();

        WebContext webContext = WebContextFactory.get();

        timer = new Timer();
        timer.schedule(new AlertTask(webContext, lastDate), 10000, 60000);
    }

   class AlertTask extends TimerTask {

       private WebContext webContext;
       private LocalDateTime lastDate;
       private long count;

       public AlertTask(WebContext webContext, LocalDateTime lastDate) {
           this.webContext = webContext;
           this.lastDate = lastDate;
       }

       @Override
       public void run() {  //referente ao agendamento de tarfas
           String session = webContext.getScriptSession().getId();

           Browser.withSession(webContext, session, new Runnable() {
               @Override
               public void run() {  //thread do DWR para utilizar o Ajax Reverso
                   Map<String, Object> map = promocaoRepository.totalAndUltimaPromocaoByDataCadastro(lastDate);

                   count = (long) map.get("count");
                   lastDate = map.get("lastaDate") == null ? lastDate : (LocalDateTime) map.get("lastaDate"); //se for nulo não tem pomoçoes novas cadastradas

                   Calendar calendar = Calendar.getInstance();
                   calendar.setTimeInMillis(webContext.getScriptSession().getLastAccessedTime()); //data da ultima tentativa de acesso da DWR do cliente js
                   System.out.println("count: " + count
                                        + ", lastDate: " + lastDate
                                        + "<" + session + "> "
                                        + " <" + calendar.getTime() + ">");

                   if(count > 0) {
                       ScriptSessions.addFunctionCall("showButton", count); //nome da função de promo-list.js
                   }
               }
           });


       }
   }
}
