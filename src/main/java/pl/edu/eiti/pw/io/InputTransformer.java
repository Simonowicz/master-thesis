package pl.edu.eiti.pw.io;

import java.io.IOException;

/**
 * Interface for source
 */
public interface InputTransformer {
    void readInputAndSave() throws IOException;
}
