package com.gionee.autotest.field.ui.call_quality.model;

/**
 * Created by viking on 12/6/17.
 */

public class ReportFile {

    private String directory ;

    private String filePath ;

    public ReportFile(String directory, String filePath) {
        this.directory = directory;
        this.filePath = filePath;
    }

    public String getDirectory() {
        return directory;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportFile that = (ReportFile) o;

        if (directory != null ? !directory.equals(that.directory) : that.directory != null)
            return false;
        return filePath != null ? filePath.equals(that.filePath) : that.filePath == null;
    }

    @Override
    public int hashCode() {
        int result = directory != null ? directory.hashCode() : 0;
        result = 31 * result + (filePath != null ? filePath.hashCode() : 0);
        return result;
    }
}
