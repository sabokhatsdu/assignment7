import java.util.List;
import java.util.ArrayList;

class Query {
    enum Category { HARDWARE, SOFTWARE, NETWORK }

    private int queryId;
    private String queryDetail;
    private Category queryCategory;

    public Query(int queryId, String queryDetail, Category queryCategory) {
        this.queryId = queryId;
        this.queryDetail = queryDetail;
        this.queryCategory = queryCategory;
    }

    public int getQueryId() {
        return queryId;
    }

    public String getQueryDetail() {
        return queryDetail;
    }

    public Category getQueryCategory() {
        return queryCategory;
    }

    @Override
    public String toString() {
        return "Query{" +
                "queryId=" + queryId +
                ", queryDetail='" + queryDetail + '\'' +
                ", queryCategory=" + queryCategory +
                '}';
    }
}

class File {
    private String fileId;
    private String fileContent;

    public File(String fileId, String fileContent) {
        this.fileId = fileId;
        this.fileContent = fileContent;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileContent() {
        return fileContent;
    }
}

class Member {
    private String memberId;

    public Member(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
}

interface QueryHandler {
    void processQuery(Query query);
    void setNextHandler(QueryHandler handler);
}

class HardwareQueryHandler implements QueryHandler {
    private QueryHandler nextHandler;

    @Override
    public void processQuery(Query query) {
        if (query.getQueryCategory() == Query.Category.HARDWARE) {
            System.out.println("Hardware team is processing query: " + query);
        } else if (nextHandler != null) {
            nextHandler.processQuery(query);
        }
    }

    @Override
    public void setNextHandler(QueryHandler handler) {
        this.nextHandler = handler;
    }
}

class SoftwareQueryHandler implements QueryHandler {
    private QueryHandler nextHandler;

    @Override
    public void processQuery(Query query) {
        if (query.getQueryCategory() == Query.Category.SOFTWARE) {
            System.out.println("Software team is processing query: " + query);
        } else if (nextHandler != null) {
            nextHandler.processQuery(query);
        }
    }

    @Override
    public void setNextHandler(QueryHandler handler) {
        this.nextHandler = handler;
    }
}

class NetworkQueryHandler implements QueryHandler {
    private QueryHandler nextHandler;

    @Override
    public void processQuery(Query query) {
        if (query.getQueryCategory() == Query.Category.NETWORK) {
            System.out.println("Network team is processing query: " + query);
        } else if (nextHandler != null) {
            nextHandler.processQuery(query);
        }
    }

    @Override
    public void setNextHandler(QueryHandler handler) {
        this.nextHandler = handler;
    }
}

interface FileProxy {
    void uploadFile(File file, Member member);
    File downloadFile(String fileId, Member member);
    void editFile(String fileId, File newFile, Member member);
    List<File> searchFiles(String searchQuery, Member member);
}

class FileProxyImpl implements FileProxy {
    private List<File> files = new ArrayList<>();
    public QueryHandler queryHandler;

    public FileProxyImpl() {
        QueryHandler hardwareHandler = new HardwareQueryHandler();
        QueryHandler softwareHandler = new SoftwareQueryHandler();
        QueryHandler networkHandler = new NetworkQueryHandler();

        hardwareHandler.setNextHandler(softwareHandler);
        softwareHandler.setNextHandler(networkHandler);

        this.queryHandler = hardwareHandler;
    }

    @Override
    public void uploadFile(File file, Member member) {
        files.add(file);
        System.out.println("File uploaded: " + file.getFileId() + " by member: " + member.getMemberId());
    }

    @Override
    public File downloadFile(String fileId, Member member) {
        for (File file : files) {
            if (file.getFileId().equals(fileId)) {
                System.out.println("File downloaded: " + file.getFileId() + " by member: " + member.getMemberId());
                return file;
            }
        }
        return null;
    }

    @Override
    public void editFile(String fileId, File newFile, Member member) {
        for (File file : files) {
            if (file.getFileId().equals(fileId)) {
                file = newFile;
                System.out.println("File edited: " + fileId + " by member: " + member.getMemberId());
                return;
            }
        }
    }

    @Override
    public List<File> searchFiles(String searchQuery, Member member) {
        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (file.getFileContent().contains(searchQuery)) {
                result.add(file);
            }
        }
        System.out.println("Search result for '" + searchQuery + "' by member: " + member.getMemberId() + ": " + result.size() + " files found");
        return result;
    }
}

public class assignment7 {
    public static void main(String[] args) {
        QueryHandler queryHandler = new FileProxyImpl().queryHandler;
        queryHandler.processQuery(new Query(1, "Hardware issue", Query.Category.HARDWARE));
        queryHandler.processQuery(new Query(2, "Software issue", Query.Category.SOFTWARE));
        queryHandler.processQuery(new Query(3, "Network issue", Query.Category.NETWORK));

        FileProxy fileProxy = new FileProxyImpl();
        fileProxy.uploadFile(new File("file1", "Content of File 1"), new Member("member1"));
        fileProxy.downloadFile("file1", new Member("member2"));
        fileProxy.editFile("file1", new File("file1", "Updated content of File 1"), new Member("member1"));
        fileProxy.searchFiles("content", new Member("member2"));
    }
}
