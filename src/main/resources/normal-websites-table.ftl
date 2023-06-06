<table>
    <thead>
    <tr>
        <th>序号</th>
        <th>网站</th>
<#--        <th>语言</th>-->
        <th>标签</th>
        <th>添加时间</th>
        <th>备注</th>
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
<#--            <td>${website.lang!""}</td>-->
            <td>
                <#if website.features??>
                    <#list website.features as feature>
                        ${feature.label!""}
                    </#list>
                </#if>
            </td>
            <td>${website.addedDate!""}</td>
            <td>${website.customDescription!""}</td>
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
