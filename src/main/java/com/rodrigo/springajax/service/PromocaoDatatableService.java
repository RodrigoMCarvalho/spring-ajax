package com.rodrigo.springajax.service;

import com.rodrigo.springajax.domain.Promocao;
import com.rodrigo.springajax.repository.PromocaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class PromocaoDatatableService {

    private String[] cols = {
         "id", "titulo", "site", "linkPromocao", "descricao",
            "linkImagem","preco", "likes", "dtCadastro", "categoria"
    };

    public Map<String, Object> execute(PromocaoRepository repository, HttpServletRequest request) {
        System.out.println(request.getContentType());
        int start = Integer.parseInt(request.getParameter("start"));
        int length = Integer.parseInt(request.getParameter("length"));
        int draw = Integer.parseInt(request.getParameter("draw"));

        int current = currentPage(start, length);

        String column = columnName(request);
        Direction direction = orderBy(request);
        String search = searchBy(request);

        Pageable pageable = PageRequest.of(current, length, direction, column);

        Page<Promocao> page = queryBy(search, repository, pageable);

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("draw", draw);
        json.put("recordsTotal", page.getTotalElements());
        json.put("recordsFiltered", page.getTotalElements());
        json.put("data", page.getContent());

        return json;
    }

    private Page<Promocao> queryBy(String search, PromocaoRepository repository, Pageable pageable) {
        if(search.isEmpty()) {
            return repository.findAll(pageable);
        }
        if(search.matches("^[0-9]+([.,][0-9]{2})?$")) {
            search = search.replace(",",".");
            return repository.findByPreco(new BigDecimal(search), pageable);
        }
        return repository.findByTituloOrSiteOrCategoria(search, pageable);
    }

    private String searchBy(HttpServletRequest request) {
        if(request.getParameter("search[value]").isEmpty()) {   //se o parametro search do DataTable tiver vazio
            return "";
        } else {
            return request.getParameter("search[value]");
        }
    }

    private Direction orderBy(HttpServletRequest request) {
        String order = request.getParameter("order[0][dir]");
        Direction sort = Direction.ASC;
        if(order.equalsIgnoreCase("desc")) {
            sort = Direction.DESC;
        }
        return sort;
    }

    private String columnName(HttpServletRequest request) {
        int iCol = Integer.parseInt(request.getParameter("order[0][column]"));
        return cols[iCol];
    }

    private int currentPage(int start, int length) {
        //0       1         2
        //0-9  |  10-19   | 20-29
        return start / length;
    }
}
