
var pageNumber = 0;

$(document).ready(function() {
    $("#loader-img").hide();
    $("#fim-btn").hide();
});

//efeito infinite scroll
$(window).scroll(function() {
    var scrollTop = $(this).scrollTop();
    var conteudo = $(document).height() - $(window).height();
    //console.log("scrolltop: ", scrollTop, " | ", "conteudo: ", conteudo);

    if (scrollTop >= conteudo) {
        //console.log("****");
        pageNumber++;
        setTimeout(function() {
            loadByScrollBar(pageNumber);
        }, 200);
    }
});

//paginacao
function loadByScrollBar(pageNumber) {
    var site = $("#autocomplete-input").val();

    $.ajax({
        method: "GET",
        url: "/promocao/list/ajax",
        data: {
            page: pageNumber,
            site: site
        },
        beforeSend: function() {
            $("#loader-img").show();
        },
        success: function(response) {
            //console.log("response: ", response);
            console.log("lista: ", response.length);

            if(response.length > 150) {
                $(".row").fadeIn(250, function() {
                    $(this).append(response);
                });
            } else {
                $("#fim-btn").show();
                $("#loader-img").removeClass("loader");
            }
        },
        error: function(xhr) {
            alert("Ops, ocorreu um erro: ", xhr.status + xhr.statusText);
        },
        complete: function() {
            $("#loader-img").hide();
        }
    })
}

//autocomplete
$("#autocomplete-input").autocomplete({
    source: function(request, response) {
        $.ajax({
            method: "GET",
            url: "/promocao/site",
            data: {
                termo: request.term
            },
            success: function(result) {
                response(result);
            }
        });
    }
});

//busca
$("#autocomplete-submit").on("click", function() {
    var site = $("#autocomplete-input").val();

    $.ajax({
        method: "GET",
        url: "/promocao/site/list",
        data: {
            site: site
        },
        beforeSend: function() {
            pageNumber = 0;
            $("#fim-btn").hide();
            $(".row").fadeOut(400, function() {
                $(this).empty();  /*desaparece com os cards*/
            });
        },
        success : function(response) {
            $(".row").fadeIn(250, function() {
                $(this).append(response);  //objeto de resposta do backend com a lista de sites
            })
        },
        error: function(xhr) {
            alert("Ops, algo deu errado: " + xhr.status + xhr.statusText);
        }
    });
});

//adicionar likes
$(document).on("click", "button[id*='likes-btn-']", function() {
    var id = $(this).attr("id").split("-")[2];
    console.log(id);

    $.ajax({
        method: "POST",
        url: "/promocao/likes/" + id,
        success: function(response) {
            $("#likes-count-" + id).text(response);
        },
        error: function(xhr) {
            alert("Ops, ocorreu um erro: " + xhr.status + ", " + xhr.statusText);
        }
    });
});

/***** AJAX Reverse *****/
var totalOfertas = 0;

$(document).ready(function() {
    init();
});


function init() {
    console.log("dwr init");

    dwr.engine.setActiveReverseAjax(true);
    dwr.engine.setErrorHandler(error);

    DWRAlertPromocoes.init();
}

function error(exception) {
    console.log("dwr error: ", exception);
}

function showButton(count) {
    totalOfertas = count;
    $("#btn-alert").show(function() {
        $(this)
            .attr("style", "display: block")
            .text("Veja " + totalOfertas + " nova(s) oferta(s)!");
    });
}

$("#btn-alert").on("click", function() {

    $.ajax({
        method: "GET",
        url: "/promocao/list/ajax",
        data: {
            page: 0
        },
        beforeSend: function() {
            pageNumber = 0;
            totalOfertas = 0;
            $("#fim-btn").hide();
            $("#loader-img").addClass("loader");
            $("#btn-alert").attr("style", "display: none;");
            $(".row").fadeOut(400, function() {
                $(this).empty();  /*desaparece com os cards*/
            });
        },
        success : function(response) {
            $("#loader-img").removeClass("loader");
            $(".row").fadeIn(250, function() {
                $(this).append(response);  //objeto de resposta do backend com a lista de sites
            })
        },
        error: function(xhr) {
            alert("Ops, algo deu errado: " + xhr.status + xhr.statusText);
        }
    });
});