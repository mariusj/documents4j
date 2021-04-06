package com.documents4j.conversion.msoffice;

import com.documents4j.api.DocumentType;
import com.documents4j.conversion.ViableConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.documents4j.api.DocumentType.Value.*;

/**
 * A converter back-end for MS Excel.
 */
@ViableConversion(from = { APPLICATION + "/" + PPT, APPLICATION + "/" + PPTX,
        APPLICATION + "/" + POWERPOINT_ANY }, to = { APPLICATION + "/" + PDF, APPLICATION + "/" + PPT,
                APPLICATION + "/" + PPTX })
public class MicrosoftPowerpointBridge extends AbstractMicrosoftOfficeBridge {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrosoftPowerpointBridge.class);

    private static final Object POWERPOINT_LOCK = new Object();

    /**
     * Other than MS Word, MS Excel does not behave well under stress. Thus, MS
     * Excel must not be asked to convert more than one document at a time.
     */
    private static final Semaphore CONVERSION_LOCK = new Semaphore(1, true);

    public MicrosoftPowerpointBridge(File baseFolder, long processTimeout, TimeUnit processTimeoutUnit) {
        super(baseFolder, processTimeout, processTimeoutUnit, MicrosoftPowerpointScript.CONVERSION);
        startUp();
    }

    private void startUp() {
        synchronized (POWERPOINT_LOCK) {
            tryStart(MicrosoftPowerpointScript.STARTUP);
            LOGGER.info("From-Microsoft-Powerpoint-Converter was started successfully");
        }
    }

    @Override
    public void shutDown() {
        synchronized (POWERPOINT_LOCK) {
            tryStop(MicrosoftPowerpointScript.SHUTDOWN);
            LOGGER.info("From-Microsoft-Powerpoint-Converter was shut down successfully");
        }
    }

    @Override
    protected MicrosoftOfficeTargetNameCorrector targetNameCorrector(File target, String fileExtension) {
        return new MicrosoftPowerpointTargetNameCorrectorAndLockManager(target, fileExtension, CONVERSION_LOCK, LOGGER);
    }

    @Override
    protected MicrosoftPowerpointFormat formatOf(DocumentType documentType) {
        return MicrosoftPowerpointFormat.of(documentType);
    }

    @Override
    protected MicrosoftOfficeScript getAssertionScript() {
        return MicrosoftPowerpointScript.ASSERTION;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
