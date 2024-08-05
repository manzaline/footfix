import { ClassicEditor } from '@ckeditor/ckeditor5-editor-classic';
import { Autoformat } from '@ckeditor/ckeditor5-autoformat';
import { Bold, Italic } from '@ckeditor/ckeditor5-basic-styles';
import { BlockQuote } from '@ckeditor/ckeditor5-block-quote';
import { CloudServices } from '@ckeditor/ckeditor5-cloud-services';
import { EasyImage } from '@ckeditor/ckeditor5-easy-image';
import { Essentials } from '@ckeditor/ckeditor5-essentials';
import { Heading } from '@ckeditor/ckeditor5-heading';
import { Image, ImageCaption, ImageStyle, ImageToolbar, ImageUpload } from '@ckeditor/ckeditor5-image';
import { Indent } from '@ckeditor/ckeditor5-indent';
import { Link } from '@ckeditor/ckeditor5-link';
import { List } from '@ckeditor/ckeditor5-list';
import { MediaEmbed } from '@ckeditor/ckeditor5-media-embed';
import { Paragraph } from '@ckeditor/ckeditor5-paragraph';

ClassicEditor
  .create( document.querySelector( '#snippet-classic-editor' ), {
    plugins: [ Essentials,
      Autoformat,
      BlockQuote,
      Bold,
      Heading,
      Image,
      ImageCaption,
      ImageStyle,
      ImageToolbar,
      Indent,
      Italic,
      Link,
      List,
      MediaEmbed,
      Paragraph,
      EasyImage,
      ImageUpload,
      CloudServices ],
    toolbar: {
      items: [
        'undo', 'redo',
        '|', 'heading',
        '|', 'bold', 'italic',
        '|', 'link', 'uploadImage', 'insertTable', 'mediaEmbed',
        '|', 'bulletedList', 'numberedList', 'outdent', 'indent'
      ]
    },
    image: {
      toolbar: [ 'imageStyle:inline', 'imageStyle:block', 'imageStyle:side', '|', 'toggleImageCaption', 'imageTextAlternative' ]
    },
    cloudServices: {
      // This editor configuration includes the Easy Image feature.
      // Provide correct configuration values to use it.
      tokenUrl: 'https://example.com/cs-token-endpoint',
      uploadUrl: 'https://your-organization-id.cke-cs.com/easyimage/upload/'
      // Read more about Easy Image - https://ckeditor.com/docs/ckeditor5/latest/features/images/image-upload/easy-image.html.
      // For other image upload methods see the guide - https://ckeditor.com/docs/ckeditor5/latest/features/images/image-upload/image-upload.html.
    }
  } )
  .then( editor => {
    window.editor = editor;
  } )
  .catch( err => {
    console.error( err.stack );
  } );
