package platform.Model;

import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CodeService {
    @Autowired
    private CodeRepository codeRepository;

    public Code getCodeData(String id){
        Code code = codeRepository.findById(id).get();
        if (!codeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (code.isRestrictView() || code.isRestrictTime()){
            checkRestrictions(code);
        }
        return code;
    }

    public String getHtml(String id) {
        if (!codeRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            Code code = getCodeData(id);
            String response = """
                    <html>
                    <head>
                        <title>Code</title>
                        <link rel="stylesheet"
                            href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
                        <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
                        <script>hljs.initHighlightingOnLoad();</script>
                    </head>
                    <body>
                    """;
            if (code.isRestrictTime()) {
                response += "<span id=\"time_restriction\">" + code.getTime() + "</span>";
            }
            if (code.isRestrictView()) {
                response += "<span id=\"views_restriction\">" + code.getViews() + "</span>";
            }
            response += "<pre id=\"code_snippet\"><code>" + code.getCode() + "</code></pre>";
            response += "<span id=\"load_date\">" + code.getDate() + "</span></body>\n" +
                    "                </html>";
            return response;
        }
    }
    public List<Code> getLatest10() {
        List<Code> codeList = codeRepository
                .findAllByRestrictTimeAndRestrictView(false, false)
                .stream()
                .sorted(Comparator.comparing(Code::getDate).reversed())
                .limit(10)
                .toList();
        return codeList.stream().sorted(Comparator.comparing(Code::getDate).reversed()).toList();
    }

    public ResponseEntity<List<Code>> getLatestJson() {
        List<Code> codeList = getLatest10();
        return ResponseEntity.ok(codeList);
    }
    public String getLatestHtml() {
        List<Code> codeList = getLatest10();
        String response = """
                <html>
                <head>
                    <title>Latest</title>
                    <link rel="stylesheet"
                        href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
                    <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
                    <script>hljs.initHighlightingOnLoad();</script>
                </head>
                <body>
                """;
        for (int i=0;i<codeList.size();i++) {
            response += "<pre id=\"code_snippet\">"+codeList.get(i).getCode()+"</pre>";
            response += "<span id=\"load_date\">"+codeList.get(i).getDate()+"</span>";
        }
        response += "</body></html>";
        return response;
    }

    public String saveCode(Code newCode) {
        Code code = new Code();
        code.setId(UUID.randomUUID().toString());
        code.setCode(newCode.getCode());
        code.setDate(LocalDateTime.now().toString());
        code.setTime(newCode.getTime());
        code.setViews(newCode.getViews());
        code.setRestrictTime(newCode.getTime() > 0);
        code.setRestrictView(newCode.getViews() > 0);
        codeRepository.save(code);
        return "{ \"id\":\""+code.getId()+"\"}";
    }

    private void checkRestrictions(Code code){
        if (code.isRestrictView()) {
            if (code.getViews() <= 0) {
                codeRepository.deleteById(code.getId());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            code.setViews(code.getViews()-1);
        }
        if (code.isRestrictTime()) {
            long remainingTime = LocalDateTime.now().until(code.getDateTime().plusSeconds(code.getTime()), ChronoUnit.SECONDS);
            if (remainingTime <= 0) {
                codeRepository.deleteById(code.getId());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            code.setTime(remainingTime);
        }
        codeRepository.save(code);
    }
}
