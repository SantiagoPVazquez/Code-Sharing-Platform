package platform.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import platform.Model.Code;
import platform.Model.CodeService;

import java.util.List;
import java.util.Map;

@RestController
public class CodeController {

    @Autowired
    private CodeService codeService;


    // API REST methods
    @GetMapping("/api/code/{uuid}")
    public Code getCode(@PathVariable String uuid) {
        return codeService.getCodeData(uuid);
    }

    @PostMapping("/api/code/new")
    public String postCode(@RequestBody Code code) {
        return codeService.saveCode(code);
    }

    @GetMapping("/api/code/latest")
    public ResponseEntity<List<Code>> getLatestCode(){
        return codeService.getLatestJson();
    }

    //REST methods
    @GetMapping("/code/{uuid}")
    public String getHtmlCode(@PathVariable String uuid){
        return codeService.getHtml(uuid);
    }

    @GetMapping("/code/latest")
    public String getHtmlLatestCode() {
        return codeService.getLatestHtml();
    }

    @GetMapping("/code/new")
    public String getCodeNew() {
        return """
                    <html>
                        <head>
                            <title>Create</title>
                            <link rel="stylesheet"
                                href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
                            <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
                            <script>hljs.initHighlightingOnLoad();</script>
                            <script>
                                function send() {
                                    let object = {
                                        "code": document.getElementById("code_snippet").value
                                    };
                                                    
                                    let json = JSON.stringify(object);
                                                    
                                    let xhr = new XMLHttpRequest();
                                    xhr.open("POST", '/api/code/new', false)
                                    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
                                    xhr.send(json);
                                                    
                                    if (xhr.status == 200) {
                                        alert("Success!");
                                    }
                                }
                            </script>
                        </head>
                        <body>
                            <form>
                                <input id="time_restriction" type="text"/>
                                <input id="views_restriction" type="text"/>
                                <textarea id="code_snippet"> ... </textarea>
                                <button id="send_snippet" type="submit" onclick="send()">Submit</button>
                            </form>
                        </body>
                    </html>
                    """;
    }
}
