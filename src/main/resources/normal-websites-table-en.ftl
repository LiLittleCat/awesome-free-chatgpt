<table>
    <thead>
    <tr>
        <th>No.</th>
        <th>Website</th>
        <th>Language</th>
        <th>Tags</th>
        <th>Add Date</th>
        <th>Description</th>
<#--        <th>预览</th>-->
<#--        <th>操作</th>-->
    </tr>
    </thead>
    <tbody>
    <#list websites as website>
        <tr>
            <td>${website_index + 1}</td>
            <td>${website.title!""} <a href="${website.url!""}" target="_blank">${website.url!""}</a>
                <br> ${website.description!""}</td>
            <td>${website.lang!""}</td>
            <td>
                <#if website.features??>
                    <#list website.features as feature>
                        ${feature.label!""}
                    </#list>
                </#if>
            </td>
            <td>${website.addedDate!""}</td>
            <td>${website.customDescriptionEnglish!""}</td>
<#--            <td style="text-align: center">-->
<#--                <details>-->
<#--                    <summary>点击预览</summary>-->
<#--                    <img src="${website.previewUrl!""}" alt="preview">-->
<#--                </details>-->
<#--            </td>-->
<#--            <td>点赞</td>-->
        </tr>
    </#list>
    </tbody>
</table>
