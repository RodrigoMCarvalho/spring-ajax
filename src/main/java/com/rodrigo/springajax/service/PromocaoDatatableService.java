package com.rodrigo.springajax.service;

import com.rodrigo.springajax.domain.Promocao;
import com.rodrigo.springajax.repository.PromocaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

public class PromocaoDatatableService {

    private String[] cols = {
         "id", "titulo", "site", "linkPromocao", "descricao",
            "linkImagem","preco", "likes", "dtCadastro", "categoria"
    };

    public Map<String, Object> execute(PromocaoRepository repository, HttpServletRequest request) {

        int start = Integer.parseInt(request.getParameter("start"));
        int length = Integer.parseInt(request.getParameter("length"));
        int draw = Integer.parseInt(request.getParameter("draw"));

        int current = currentPage(start, length);

        String column = columnName(request);
        Direction direction = orderBy(request);

        Pageable pageable = PageRequest.of(current, length, direction, column);

        Page<Promocao> page = queryBy(repository, pageable);

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("draw", draw);
        json.put("recordsTotal", page.getTotalElements());
        json.put("recordsFiltered", page.getTotalElements());
        json.put("data", page.getContent());

        return json;
    }

    private Page<Promocao> queryBy(PromocaoRepository repository, Pageable pageable) {
        return repository.findAll(pageable);
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
        return start * length;
    }
}
