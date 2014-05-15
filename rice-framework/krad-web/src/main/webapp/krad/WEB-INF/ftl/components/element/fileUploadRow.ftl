{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-upload fade">
        <td>
            <p class="name">{%=file.name%}</p>
            <strong class="error text-danger"></strong>
            <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="progress-bar progress-bar-success" style="width:0%;"></div></div>
        </td>
        <td>
            <p class="size">Processing...</p>
        </td>
        <td>
        </td>
        <td>
            {% if (!i && !o.options.autoUpload) { %}
                <button class="btn btn-primary start" disabled>
                    <span>Upload</span>
                </button>
            {% } %}
            {% if (!i) { %}
                <button class="btn btn-default cancel" title="Cancel">
                    <i class="icon-trash"></i>
                </button>
            {% } %}
        </td>
    </tr>
{% } %}