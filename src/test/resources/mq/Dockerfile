FROM rmohr/activemq:5.15.9-alpine

USER root

COPY ./conf /opt/activemq/conf 

RUN chown -R activemq:activemq /opt/activemq/conf

USER activemq

EXPOSE $ACTIVEMQ_TCP $ACTIVEMQ_AMQP $ACTIVEMQ_STOMP $ACTIVEMQ_MQTT $ACTIVEMQ_WS $ACTIVEMQ_UI

CMD ["/bin/sh", "-c", "bin/activemq console"]