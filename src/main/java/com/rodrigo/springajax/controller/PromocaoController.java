package com.rodrigo.springajax.controller;

import com.rodrigo.springajax.domain.Categoria;
import com.rodrigo.springajax.domain.Promocao;
import com.rodrigo.springajax.repository.CategoriaRepository;
import com.rodrigo.springajax.repository.PromocaoRepository;
import com.rodrigo.springajax.service.PromocaoDatatableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/promocao")
public class PromocaoController {

    private static Logger log = LoggerFactory.getLogger(PromocaoController.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PromocaoRepository promocaoRepository;

    @PostMapping("/save")
    public ResponseEntity<?> salvarPromocao(@Valid Promocao promocao, BindingResult result) {
        if(result.hasErrors()) {

            Map<String, String> errors = new HashMap<>();
            for(FieldError error: result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.unprocessableEntity().body(errors);
        }
        log.info("Promocao {}", promocao.toString());
        promocao.setDtCadastro(LocalDateTime.now());
        promocaoRepository.save(promocao);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public String listarOfertas(ModelMap model) {
        Sort sort = Sort.by(Sort.Direction.DESC, "dtCadastro");
        PageRequest pageRequest = PageRequest.of(0,8,sort);
        model.addAttribute("promocoes", promocaoRepository.findAll(pageRequest));
        return "promo-list";
    }

    @GetMapping("/list/ajax")
    public String listarCards(@RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "site", defaultValue = "") String site,
                              ModelMap model) {
        Sort sort = Sort.by(Sort.Direction.DESC, "dtCadastro");
        PageRequest pageRequest = PageRequest.of(page,8,sort);

        if(site.isEmpty()) {  //caso não tenha sido informado o site na busca
            model.addAttribute("promocoes", promocaoRepository.findAll(pageRequest));
        } else {
            model.addAttribute("promocoes", promocaoRepository.findBySite(site, pageRequest));
        }
        return "promo-card";
    }

    @GetMapping("/site")
    public ResponseEntity<?> autocompleteByTermo(@RequestParam(name = "termo") String termo) {
        List<String> sites = promocaoRepository.findSiteByTermo(termo);
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/site/list")
    public String listarPorSites(@RequestParam("site") String site, ModelMap model) {
        Sort sort = Sort.by(Sort.Direction.DESC, "dtCadastro");
        PageRequest pageRequest = PageRequest.of(0 ,8,sort);
        model.addAttribute("promocoes", promocaoRepository.findBySite(site, pageRequest));
        return "promo-card";
    }

    @PostMapping("/likes/{id}")
    public ResponseEntity<?> adicionarLikes(@PathVariable("id") Long id) {
        promocaoRepository.updateSomarLikes(id);
        int likes = promocaoRepository.findLikesById(id);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("datatables/server")
    public ResponseEntity<?> datatables(HttpServletRequest request) {
        Map<String, Object> data = new PromocaoDatatableService().execute(promocaoRepository, request);
        return ResponseEntity.ok(data);
    }

    @ModelAttribute("categorias")
    public List<Categoria> getCategorias() {
        return categoriaRepository.findAll();
    }

    @GetMapping("/add")
    public String abrirCadastro() {
        return "promo-add";
    }

    @GetMapping("/tabela")
    public String abrirTabela() {
        return "promo-datatables";
    }
}
