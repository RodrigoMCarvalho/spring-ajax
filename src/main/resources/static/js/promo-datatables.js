$(document).ready(function() {
    
    moment.locale('pt-br');

    var table = $("#table-server").DataTable({
        language: {
                "sEmptyTable": "Nenhum registro encontrado",
                "sInfo": "Mostrando de _START_ até _END_ de _TOTAL_ registros",
                "sInfoEmpty": "Mostrando 0 até 0 de 0 registros",
                "sInfoFiltered": "(Filtrados de _MAX_ registros)",
                "sInfoPostFix": "",
                "sInfoThousands": ".",
                "sLengthMenu": "_MENU_ resultados por página",
                "sLoadingRecords": "Carregando...",
                "sProcessing": "Processando...",
                "sZeroRecords": "Nenhum registro encontrado",
                "sSearch": "Pesquisar",
                "oPaginate": {
                    "sNext": "Próximo",
                    "sPrevious": "Anterior",
                    "sFirst": "Primeiro",
                    "sLast": "Último"
                },
                "oAria": {
                    "sSortAscending": ": Ordenar colunas de forma ascendente",
                    "sSortDescending": ": Ordenar colunas de forma descendente"
                }
        },
        processing: true,
        serverSide: true,
        responsive: true,
        lengthMenu: [ 10, 15, 20, 25],
        ajax: {
            url: "/promocao/datatables/server",
            data: "data"                            /*irá representar o objeto retornado*/
        },
        columns: [
            {data: 'id'},
            {data: 'titulo'},
            {data: 'site'},
            {data: 'linkPromocao'},
            {data: 'descricao'},
            {data: 'linkImagem'},
            {data: 'preco', render: $.fn.dataTable.render.number('.', ',', 2, 'R$')},
            {data: 'likes'},
            {data: 'dtCadastro', render:
                        function(dtCadastro) {
                            return moment(dtCadastro).format("LLL");
                        }
            },
            {data: 'categoria.titulo'}
        ],
        dom: 'Bfrtip',  /*referente a lib Datatables*/
        buttons: [
            {
                text: 'Editar',
                attr: {
                    id: 'btn-editar',
                    type: 'button'
                },
                enabled: false
            },
            {
               text: 'Excluir',
               attr: {
                   id: 'btn-excluir',
                   type: 'button'
               },
               enabled: false
            }
        ],

});

    //ação para marcar/desmarcar botões ao clicar na ordenação
    $("#table-server thead").on('click', 'tr', function() {
      table.buttons().disable();
    });

    //ação para marcar/desmarcar linhas clicadas
    $("#table-server tbody").on('click', 'tr', function() {
        if($(this).hasClass('selected')) {   /* remove a seleção com o 2º clique na mesma linha*/
            $(this).removeClass('selected');
            table.buttons().disable();   /* referente a lib DataTables*/
        } else {
            $('tr.selected').removeClass('selected');   /* remove a seleção caso o usuário selecione uma nova linha */
            $(this).addClass('selected');
            table.buttons().enable();
        }
    });

    //abrir o modal de editar e popular os dados da promoção selecionada
    $("#btn-editar").on('click', function() {
        if(isSelectedRow()) {

            var id = getPromoId();
             $.ajax({
                method: "GET",
                url: "/promocao/edit/" + id,
                beforeSend: function() {
                    limparMsgErros();      
                    $("#modal-form").modal('show');
                },
                success: function(data) {
                    $("#edt_id").val(data.id);
                    $("#edt_site").val(data.site);
                    $("#edt_titulo").val(data.titulo);
                    $("#edt_descricao").val(data.descricao);
                    $("#edt_preco").val(data.preco.toLocaleString('pt-BR', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    }));
                    $("#edt_categoria").val(data.categoria.id);
                    $("#edt_linkImagem").val(data.linkImagem);
                    $("#edt_imagem").attr('src', data.linkImagem);
                },
                error: function() {
                   alert("Ops...ocorreu um erro, tente novamente mais tarde.")
                }
             });
        }
    });

    //submit do formulario editar
    $('#btn-edit-modal').on('click', function() {
        var promo = {};

        promo.id = $("#edt_id").val();
        promo.descricao = $("#edt_descricao").val();
        promo.preco = $("#edt_preco").val();
        promo.titulo = $("#edt_titulo").val();
        promo.categoria = $("#edt_categoria").val();
        promo.linkImagem = $("#edt_linkImagem").val();
        console.log('promo: ', promo);

        $.ajax({
            method: "POST",
            url: "/promocao/edit",
            data: promo,
            beforeSend: function() {
                limparMsgErros();    //limpa as mensagens de erro de validação, para que as mesmas não fiquem repetidas
            },
            success: function() {
                $("#modal-form").modal("hide");
                table.ajax.reload();
            },
            statusCode: {
                422: function(xhr) {
                    console.log("status error: ", xhr.status);
                    var errors = $.parseJSON(xhr.responseText);
                    $.each(errors, function(key, value) {
                        $("#edt_"+ key).addClass("is-invalid");
                        $("#error-" + key)
                            .addClass("invalid-feedback")
                            .append("<span class='error-span'>" + value + "</span>");
                    });
                }
            },
        });

    });

    //alterar a imagem no componente <img> do modal
    $("#edt_linkImagem").on("change", function() {
        var link = $(this).val();
        $("#edt_imagem").attr("src", link);
    });

    //ação do botão excluir (abrir modal)
    $("#btn-excluir").on('click', function() {
         if(isSelectedRow()) {
            $("#modal-delete").modal('show');
          }
    });

    //exclusão de uma promoção (dentro do modal)
    $("#btn-del-modal").on('click', function() {
        var id = getPromoId();
        $.ajax({
            method: "GET",
            url: "/promocao/delete/" + id,
            success: function() {
                $("#modal-delete").modal("hide");
                table.ajax.reload();
            },
            error: function() {
                alert("Ops...ocorreu um erro, tente novamente mais tarde.")
            }
        });
    });

    //obtem o id da linha selecionada na tabela
    function getPromoId() {
        return table.row(table.$('tr.selected')).data().id;
    }

    function isSelectedRow() {
        var trow = table.row(table.$('tr.selected'));
        return trow.data() !== undefined;
    }

    function limparMsgErros() {
        //removendo as mensagens de validacao das classes error-span
        $("span").closest('.error-span').remove();

        //removendo as bordas vermelhas
        $(".is-invalid").removeClass("is-invalid");
    }

    
}); 