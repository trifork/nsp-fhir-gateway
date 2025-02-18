package dk.medcom.edelivery.integration.dgws;

import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import dk.medcom.edelivery.integration.nas.Notification;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import javax.xml.bind.JAXBException;

public class AugmentedJAXBContextInterceptor extends AbstractPhaseInterceptor<Message> {

    public AugmentedJAXBContextInterceptor() {
        super(Phase.PRE_MARSHAL);
    }

    @Override
    public void handleMessage(Message message) {
        JAXBDataBinding jaxbDataBinding = getDataBinding(message);

        JAXBContextImpl augmented = getAugmentedJaxbContext(jaxbDataBinding);

        jaxbDataBinding.setContext(augmented);
    }

    private JAXBContextImpl getAugmentedJaxbContext(JAXBDataBinding jaxbDataBinding) {
        try {
            return getJaxbContext(jaxbDataBinding).createAugmented(Notification.class);
        } catch (JAXBException e) {
            throw new Fault(e);
        }
    }

    private JAXBContextImpl getJaxbContext(JAXBDataBinding jaxbDataBinding) {
        var jaxbContext = jaxbDataBinding.getContext();
        return (JAXBContextImpl) jaxbContext;
    }

    private JAXBDataBinding getDataBinding(Message message) {
        var dataBinding = message.getExchange().getService().getDataBinding();
        return (JAXBDataBinding) dataBinding;
    }
}
