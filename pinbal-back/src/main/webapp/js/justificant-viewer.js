// Canviar entre obert i tancat de la previsualització del jsutificant
const toggleDocJustificant = (tr) => {

    let rowId = $(tr).attr('id');
    // Ocultam tots els detalls que es trobin actualment oberts (en principi només n'hi pot haver un)
    tr.parent().find('tr').each((index, row) => {
        // Comprovam si el detall del document actual està obert
        let justificantActualVisible = $(row).next().hasClass('doc-justificant') && $(row).next().is(":visible");
        if ($(row).attr('id') == rowId && !justificantActualVisible ) {
            showJustificantRow(row);
        } else {
            closeJustificantRow(row);
        }
    });
}

// Tancar la fila amb el justificant
const closeJustificantRow = (detail) => {
    // Per tancar el detall dels documents ho feim amb un efecte d'acordiò de 500ms
    if ($(detail).next().hasClass('doc-justificant')) {
        slideRowUp($(detail).next(), 500);
    }
}

// Mostrar la fila amb el justificant
const showJustificantRow = (row) => {
    // Si ja s'havia carregat el detall, el tornem a obrir amb un efecte d'acordió de 500ms
    if ($(row).next().hasClass('doc-justificant')) {
        slideRowDown($(row).next(), 500);

        // Si encara no s'havia carregat el justificant, l'hem de crear
    } else {
        createDocJustificant(row);
    }
}

// Crea la fila amb el detall del document
const createDocJustificant = (row) => {
    addRowJustificant(row);
    let viewer = $(row).next().find('.viewer');
    if ($(viewer).data('loaded') == undefined) {
        const urlJustificant = $(row).find('.btn-justificant').attr('href') + '/previsualitzacio';
        showJustificant(viewer, urlJustificant);
    }
}

// Afegim dues files per a mantenir els colors de parells i senars de la taula.
// En aquest mètode mostram l'spinner de càrrega
const addRowJustificant = (row) => {
    $(row).after('<tr style="display: none;><td colspan="9"></td></tr>');
    $(row).after('<tr class="doc-justificant"><td class="td-doc-justificant" colspan="9"><div class="viewer" style="display: none; width: 100%;"><iframe class="viewer-iframe" width="100%" height="540" frameBorder="0" style="padding: 15px;"></iframe></div></td></tr>');
    slideRowDown($(row).next(), 200);
}

// Ocultam la fila del justificant amb un efecte d'slide.
const slideRowUp = (row, duration, funcio) => {
    $('.viewer', $(row)).slideUp(duration, function(){
        $(row).hide();
        if (funcio != undefined) {
            funcio();
        }
    });
}

// Visualitzan la fila del justificant amb un efecte d'slide
const slideRowDown = (row, duration, funcio) => {
    $(row).show();
    $('.viewer', $(row)).slideDown(duration, () => {
        if (funcio != undefined) {
            funcio();
        }
    });
}

const showJustificant = (viewer, urlJustificant) => {

    let contenidor = $(viewer).find('iframe');
    $(viewer).prepend('<span class="fa fa-circle-notch fa-spin"></span>');
    contenidor.addClass('rmodal_loading');

    // Fa la petició a l'url de l'arxiu
    $.ajax({
        type: 'GET',
        url: urlJustificant,
        responseType: 'arraybuffer',
        success: function(json) {
            if (json.error) {
                $(viewer).before('<div class="viewer-padding"><div class="alert alert-danger">' + msgViewer['error'] + ': ' + json.errorMsg + '</div></div>');
            } else if (json.warning) {
                $(viewer).before('<div class="viewer-padding"><div class="alert alert-warning">' + msgViewer['warning'] + '</div></div>');
            } else {
                const response = json.data;
                const blob = base64toBlob(response.contingut, response.contentType);
                const file = new File([blob], response.contentType, {type: response.contentType});
                const link = URL.createObjectURL(file);

                const viewerUrl = urlViewer + '?file=' + encodeURIComponent(link);
                contenidor.attr('src', viewerUrl);
                $(viewer).data('loaded', "true");
            }
            $(viewer).find('.fa-spin').remove();

        },
        error: function(xhr, ajaxOptions, thrownError) {
            $(viewer).find('.fa-spin').remove();
            alert(thrownError);
        }
    });
}

const base64toBlob = (b64Data, contentType) => {
    var contentType = contentType || '';
    var sliceSize = 512;
    var byteCharacters = atob(b64Data);
    var byteArrays = [];
    for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
        var slice = byteCharacters.slice(offset, offset + sliceSize);
        var byteNumbers = new Array(slice.length);
        for (var i=0; i<slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }
        var byteArray = new Uint8Array(byteNumbers);
        byteArrays.push(byteArray);
    }
    var blob = new Blob(byteArrays, {type: contentType});
    return blob;
}

const modalAjaxErrorFunction = (jqXHR, exception) => {
    if (jqXHR.status === 0) {
        alert('Not connected.\n Verify network.');
    } else if (jqXHR.status == 404) {
        alert('Requested page not found [404].');
    } else if (jqXHR.status == 500) {
        alert('Internal server error [500].');
    } else if (exception === 'parsererror') {
        alert('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
        alert('Timeout error.');
    } else if (exception === 'abort') {
        alert('Ajax request aborted.');
    } else {
        alert('Unknown error:\n' + jqXHR.responseText);
    }
}