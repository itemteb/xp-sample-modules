var view = resolve('/cms/view/trampoline-accessories.xsl');

function handleGet(req) {

    var component = execute('portal.getComponent');
    
    var editMode = req.mode == 'edit';
    var model = {
        title: req.content.displayName,
        componentType: component.type,
        editable: editMode
    };

    var body = execute('xslt.render', {
        view: view,
        model: model
    });

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
