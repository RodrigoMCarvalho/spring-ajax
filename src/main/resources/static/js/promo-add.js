//submit do formulario para o controller
$("#form-add-promo").submit(function(evt) {
    //bloquear o comportamento padrao do submit
    evt.preventDefault();

    var promo = {};
    promo.linkPromocao = $("#linkPromocao").val();
    promo.descricao = $("#descricao").val();
    promo.preco = $("#preco").val();
    promo.titulo = $("#titulo").val();
    promo.categoria = $("#categoria").val();
    promo.linkImagem = $("#linkImagem").attr("src");
    promo.site = $("#site").text();
    console.log('promo: ', promo);

    $.ajax({
        method: "POST",
        url: "/promocao/save",
        data: promo,
        beforeSend: function() {
            //removendo as mensagens de validacao das classes error-span
            $("span").closest('.error-span').remove();

            //removendo as bordas vermelhas
            $("#categoria").removeClass("is-invalid");
            $("#preco").removeClass("is-invalid");
            $("#linkPromocao").removeClass("is-invalid");
            $("#titulo").removeClass("is-invalid");

            //habilita o loading
            $("#form-add-promo").hide();
            $("#loader-form").addClass("loader").show();
        },
        success: function() {
            $("#form-add-promo").each(function() {
                this.reset();
            });
            $("#site").text("");
            $("#linkImagem").attr("src", "/images/promo-dark.png");
            $("#alert")
                .removeClass("alert alert-danger")  /*remove a cor vermelha após validacao e novo salvamento*/
                .addClass("alert alert-success")
                .text("Ok! Promoção cadastrada com sucesso!");
        },
        statusCode: {
            422: function(xhr) {
                console.log("status error: ", xhr.status);
                var errors = $.parseJSON(xhr.responseText);
                $.each(errors, function(key, value) {
                    $("#"+ key).addClass("is-invalid");
                    $("#error-" + key)
                        .addClass("invalid-feedback")
                        .append("<span class='error-span'>" + value + "</span>");
                });
            }
        },
        error: function(xhr) {
            console.log("erro: ", xhr.responseText)
            $("#alert").addClass("alert alert-danger").text("Não foi possível salvar esta promoção.");
        },
        complete: function() {
            $("#loader-form").fadeOut(800, function() {
                $("#form-add-promo").fadeIn(250);
                $("#loader-form").removeClass("loader");
            })
        }
    })
})

//funcao para capturar as meta tags
$("#linkPromocao").on("change", function() {
  var url = $(this).val();

  if(url.length > 7) {
    $.ajax({
        method: "POST",
        url: "/meta/info?url=" + url,
        cache: false,
        beforeSend: function() {
           $("#alert").removeClass("alert alert-danger alert-success").text("");
           $("#titulo").val("");
           $("#site").text("");
           $("#linkImagem").attr("src", "");
           $("#loader-img").addClass("loader");
        },
        success: function(data) {
            console.log(data);
            $("#titulo").val(data.title);
            $("#site").text(data.site.replace("@",""));
            $("#linkImagem").attr("src", data.image);
        },
        statusCode: {
            404: function() {
                $("#alert").addClass("alert alert-danger").text("Nenhuma informação pode ser recuperada desta URL.");
                $("#linkImagem").attr("src", "/images/promo-dark.png");
            }
        },
        error: function() {
            $("#alert").addClass("alert alert-danger").text("Ops...algo deu errado, tente mais tarde.");
            $("#linkImagem").attr("src", "/images/promo-dark.png");
        },
        complete: function() {
            $("#loader-img").removeClass("loader");
        }
    })
  }
})