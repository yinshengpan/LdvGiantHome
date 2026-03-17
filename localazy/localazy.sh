#!/bin/bash

# ========= CONFIG =========
CONVERSION_DIR="localazy/conversion"
RES_DIR="core/ui/src/main/res"
# ==========================

echo "🚀 正在执行: 从 Localazy 下载多语言字符串"
mkdir -p "$CONVERSION_DIR"
java -jar localazy/localazy.jar download -q --config localazy/localazy.json
if [ $? -ne 0 ]; then
  echo "❌ 从 Localazy 下载多语言字符串失败，终止后续流程。"
  exit 1
fi
echo "✅ 从 Localazy 下载多语言字符串完成"
echo
echo "🔄 开始转换 strings.json 为 strings.xml..."

find "$CONVERSION_DIR" -type f -name "strings.json" | while read -r jsonFile; do
    dirPath=$(dirname "$jsonFile")
    # 只对特定路径进行转换
    if [ "$dirPath" = "localazy/conversion/values-es-r419" ]; then
        newDirPath="localazy/conversion/values-b+es+419"
    else
        # 其他路径保持不变
        newDirPath="$dirPath"
    fi

    mkdir -p "$newDirPath"
    xmlFile="$newDirPath/strings.xml"

    echo "🛠️  处理: $jsonFile"

    echo '<?xml version="1.0" encoding="utf-8"?>' > "$xmlFile"
    echo '<resources>' >> "$xmlFile"

    # 移除花括号和空行，逐行提取 key-value
    grep ':' "$jsonFile" | sed -E 's/^[ \t]*"([^"]+)"[ \t]*:[ \t]*"(.*)",?[ \t]*$/\1|\2/' | while IFS='|' read -r key value; do
        # 去掉末尾引号
        value=$(printf "$value" | sed 's/"$//')

        # XML 转义
        value=$(printf "$value" \
          | sed -e 's/&/\&amp;/g' \
                -e 's/</\&lt;/g' \
                -e 's/>/\&gt;/g')

        # 占位符替换
        value=$(printf "$value" \
          | sed -e 's/\[\[placeholder1\]\]/%1\$s/g' \
                -e 's/\[\[placeholder2\]\]/%2\$s/g' \
                -e 's/\[\[placeholder3\]\]/%3\$s/g' \
                -e 's/\[\[placeholder4\]\]/%4\$s/g')

        echo "    <string name=\"$key\">\"$value\"</string>" >> "$xmlFile"
    done

    echo '</resources>' >> "$xmlFile"
    echo "✅ 生成: $xmlFile"
done
echo
echo "📦 开始复制 strings.xml 到项目 $RES_DIR 目录..."

find "$CONVERSION_DIR" -maxdepth 1 -type d -name "values*" | while read -r langDir; do
    dirName=$(basename "$langDir")
    targetDir="$RES_DIR/$dirName"
    if [ -f "$langDir/strings.xml" ]; then
      echo "🔁 替换 $targetDir"
      mkdir -p "$targetDir"
      cp "$langDir/strings.xml" "$targetDir/strings.xml"
      echo "✅ 已复制到: $targetDir"
    fi
done

rm -rf "${CONVERSION_DIR}"
echo "🎉 所有操作完成！strings.xml 已更新到 $RES_DIR"
