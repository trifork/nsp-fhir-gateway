package dk.medcom.edelivery.ws;

import org.openehealth.ipf.commons.ihe.xds.core.requests.DocumentReference;
import org.openehealth.ipf.commons.ihe.xds.core.responses.ErrorCode;
import org.openehealth.ipf.commons.ihe.xds.core.responses.ErrorInfo;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Severity;

public class RetrieveDocumentError extends RuntimeException {

    private final ErrorInfo errorInfo;

    public static RetrieveDocumentError documentNotFound(DocumentReference reference) {
        return new RetrieveDocumentError(new ErrorInfo(
                ErrorCode.DOCUMENT_UNIQUE_ID_ERROR,
                String.format("Document with unique id '%s' is not available.", reference.getDocumentUniqueId()),
                Severity.ERROR,
                null,
                null
        ));
    }

    public static RetrieveDocumentError invalidHomeCommunity(DocumentReference documentReference) {
        return new RetrieveDocumentError(new ErrorInfo(
                ErrorCode.INVALID_REQUEST_EXCEPTION,
                String.format("Home Community ID '%s' is not known.", documentReference.getHomeCommunityId()),
                Severity.ERROR,
                null,
                null
        ));
    }

    public static RetrieveDocumentError invalidRepository(DocumentReference documentReference) {
        return new RetrieveDocumentError(new ErrorInfo(
                ErrorCode.INVALID_REQUEST_EXCEPTION,
                String.format("Repository unique ID '%s' is not known.", documentReference.getRepositoryUniqueId()),
                Severity.ERROR,
                null,
                null
        ));
    }

    public RetrieveDocumentError(ErrorInfo errorInfo) {
        super(errorInfo.getCodeContext());
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

}
